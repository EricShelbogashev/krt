package raytracer.model

interface Traceable {
    fun hit(ray: Ray, tMin: Double, tMax: Double): Hit?
    fun boundingBox(time0: Double, time1: Double): AABB?
    fun translate(offset: Point3)
    fun id(): String? = null
}