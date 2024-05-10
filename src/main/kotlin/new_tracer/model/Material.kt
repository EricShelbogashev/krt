package new_tracer.model

import new_tracer.algebra.Color
import new_tracer.algebra.Point3

interface Material {
    fun emitted(ray: Ray, hit: Hit, u: Double, v: Double, p: Point3): Color = Color.ZERO
    fun scatter(ray: Ray, hit: Hit): Scattered? = null
}

data class Scattered(
    val specularRay: Ray,
    val isSpecular: Boolean,
    val attenuation: Color,
)