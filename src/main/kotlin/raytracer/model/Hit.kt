package raytracer.model

data class Hit(
    val p: Point3,
    val t: Double,
    val normal: Point3,
    val isFrontFace: Boolean,
    val material: Material
) {
    companion object {
        fun create(p: Point3, t: Double, ray: Ray, outwardNormal: Point3, material: Material): Hit {
            val isFrontFace = ray.direction.dot(outwardNormal) < 0
            val normal = if (isFrontFace) outwardNormal else -outwardNormal
            return Hit(p, t, normal, isFrontFace, material)
        }
    }

    fun scatter(ray: Ray): Scattered? = material.scatter(ray, this)
}