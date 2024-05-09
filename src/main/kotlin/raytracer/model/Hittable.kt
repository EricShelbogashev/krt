package raytracer.model

interface Hittable {
    fun hit(ray: Ray, tMin: Double, tMax: Double): Hit?
    fun boundingBox(time0: Double, time1: Double): AABB?
}