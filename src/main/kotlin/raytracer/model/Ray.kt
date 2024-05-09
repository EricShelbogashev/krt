package raytracer.model

class Ray(val origin: Point3, val direction: Point3) {
    fun at(t: Double): Point3 = origin + direction * t
}
