package raytracer.model

import java.util.*
import kotlin.math.cos
import kotlin.math.sin
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

        // Generate horizontal circles
        for (i in 0 until steps) {
            val theta1 = Math.PI * 2 * i / steps
            val theta2 = Math.PI * 2 * (i + 1) / steps
            for (j in 0 until steps) {
                val phi = Math.PI * j / steps
                val p1 = center + Point3(
                    radius * cos(theta1) * sin(phi),
                    radius * sin(theta1) * sin(phi),
                    radius * cos(phi)
                )
                val p2 = center + Point3(
                    radius * cos(theta2) * sin(phi),
                    radius * sin(theta2) * sin(phi),
                    radius * cos(phi)
                )
                segments.add(LineSegment(p1, p2))
            }
        }

        // Generate vertical circles
        for (i in 0 until steps) {
            val theta = Math.PI * 2 * i / steps
            for (j in 0 until steps) {
                val phi1 = Math.PI * j / steps
                val phi2 = Math.PI * (j + 1) / steps
                val p1 = center + Point3(
                    radius * cos(theta) * sin(phi1),
                    radius * sin(theta) * sin(phi1),
                    radius * cos(phi1)
                )
                val p2 = center + Point3(
                    radius * cos(theta) * sin(phi2),
                    radius * sin(theta) * sin(phi2),
                    radius * cos(phi2)
                )
                segments.add(LineSegment(p1, p2))
            }
        }

        return segments
    }
}
