package raytracer.model

import java.util.*
import kotlin.math.sqrt

open class Sphere(private val center: Point3, private val radius: Double, private val material: Material) : Traceable {
    private val uuid by lazy { UUID.randomUUID() }

    override fun hit(ray: Ray, tMin: Double, tMax: Double): Hit? {
        val oc = ray.origin - center
        val a = ray.direction.dot(ray.direction)
        val halfB = oc.dot(ray.direction)
        val c = oc.dot(oc) - radius * radius
        val discriminant = halfB * halfB - a * c

        if (discriminant < 0) {
            return null
        }

        val sqrtD = sqrt(discriminant)
        var root = (-halfB - sqrtD) / a
        if (root < tMin || root > tMax) {
            root = (-halfB + sqrtD) / a
            if (root < tMin || root > tMax) {
                return null
            }
        }
        val hitPoint = ray.at(root)
        val outwardNormal = (hitPoint - center) / radius
        return Hit.create(hitPoint, root, ray, outwardNormal, material)
    }

    override fun translate(offset: Point3) {
        center.translate(offset)
    }

    override fun id(): String {
        return "Sphere[$uuid]"
    }

    override fun linearize(coefficient: Double): List<LineSegment> {
        val segments = mutableListOf<LineSegment>()
        val steps = (coefficient * 10).toInt() // Adjust this multiplier for more or fewer segments
        for (i in 0 until steps) {
            val theta1 = Math.PI * 2 * i / steps
            val theta2 = Math.PI * 2 * (i + 1) / steps
            val p1 = center + Point3(radius * Math.cos(theta1), radius * Math.sin(theta1), 0.0)
            val p2 = center + Point3(radius * Math.cos(theta2), radius * Math.sin(theta2), 0.0)
            segments.add(LineSegment(p1, p2))
        }
        return segments
    }
}
