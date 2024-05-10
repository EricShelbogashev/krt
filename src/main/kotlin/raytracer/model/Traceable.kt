package raytracer.model

interface Traceable {
    fun hit(ray: Ray, tMin: Double, tMax: Double): Hit?
    fun translate(offset: Point3)
    fun id(): String? = null
}