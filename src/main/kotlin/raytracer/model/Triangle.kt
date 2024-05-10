package raytracer.model

import java.util.*

class Triangle(
    private val v0: Point3,
    private val v1: Point3,
    private val v2: Point3,
    private val material: Material,
) : Traceable {
    private val uuid by lazy { UUID.randomUUID() }

    override fun hit(ray: Ray, tMin: Double, tMax: Double): Hit? {
        val edge1 = v1 - v0
        val edge2 = v2 - v0
        val h = ray.direction.cross(edge2)
        val a = edge1.dot(h)

        if (a > -1e-10 && a < 1e-10) {
            return null
        }

        val f = 1.0 / a
        val s = ray.origin - v0
        val u = f * (s.dot(h))

        if (u < 0.0 || u > 1.0) {
            return null
        }

        val q = s.cross(edge1)
        val v = f * ray.direction.dot(q)

        if (v < 0.0 || u + v > 1.0) {
            return null
        }

        val t = f * edge2.dot(q)
        if (t < tMin || t > tMax) {
            return null
        }

        val hitPoint = ray.at(t)
        val outwardNormal = edge1.cross(edge2).unit()
        return Hit.create(hitPoint, t, ray, outwardNormal, material)
    }

    override fun boundingBox(time0: Double, time1: Double): AABB {
        val minX = minOf(v0.x, v1.x, v2.x)
        val minY = minOf(v0.y, v1.y, v2.y)
        val minZ = minOf(v0.z, v1.z, v2.z)
        val maxX = maxOf(v0.x, v1.x, v2.x)
        val maxY = maxOf(v0.y, v1.y, v2.y)
        val maxZ = maxOf(v0.z, v1.z, v2.z)

        val minPoint = Point3(minX, minY, minZ)
        val maxPoint = Point3(maxX, maxY, maxZ)

        return AABB(minPoint, maxPoint)
    }

    override fun translate(offset: Point3) {
        v0.translate(offset)
        v1.translate(offset)
        v2.translate(offset)
    }

    override fun id(): String {
        return "Triangle[$uuid]"
    }
}
