package new_tracer.model

import new_tracer.algebra.Point3

interface Hittable {
    fun hit(ray: Ray, tMin: Double, tMax: Double): Hit? = null
//    fun boundingBox(time0: Double, time1: Double): AABB? = null
}

data class Hit(
    val p: Point3,
    var normal: Point3,
    val material: Material,
    val t: Double,
    val u: Double,
    val v: Double,
    var frontFace: Boolean,
) {
    fun setFaceNormal(ray: Ray, outwardNormal: Point3) {
        frontFace = ray.direction.dot(outwardNormal) < 0
        normal = if (frontFace) outwardNormal else -outwardNormal
    }
}

data class FlipFace(val hittable: Hittable) : Hittable {
    override fun hit(ray: Ray, tMin: Double, tMax: Double): Hit? {
        val hit = super.hit(ray, tMin, tMax) ?: return null

        hit.frontFace = !hit.frontFace
        return hit
    }
}

