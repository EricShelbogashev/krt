package raytracer.model

import kotlin.math.sqrt

class Sphere(private val center: Point3, private val radius: Double, private val material: Material) : Hittable {

    override fun hit(ray: Ray, timeMin: Double, timeMax: Double): Hit? {
        val oc = ray.origin - center
        val a = ray.direction.dot(ray.direction)  // Corrected for accurate squared magnitude of direction
        val halfB = oc.dot(ray.direction)
        val c = oc.dot(oc) - radius * radius
        val discriminant = halfB * halfB - a * c

        if (discriminant < 0) {
            return null  // No intersection
        }

        val sqrtD = sqrt(discriminant)
        // First root
        var root = (-halfB - sqrtD) / a
        if (root < timeMin || root > timeMax) {
            // Second root
            root = (-halfB + sqrtD) / a
            if (root < timeMin || root > timeMax) {
                return null  // No valid root within bounds
            }
        }
        val hitPoint = ray.at(root)
        val outwardNormal = (hitPoint - center) / radius  // Normalized normal vector
        return Hit.create(hitPoint, root, ray, outwardNormal, material)
    }

    override fun boundingBox(time0: Double, time1: Double): AABB? {
        // Simple bounding box calculation assuming static sphere
        val offset = Point3(radius, radius, radius)
        return AABB(center - offset, center + offset)
    }
}
