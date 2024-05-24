package raytracer.model

class Emissive(private val emitColor: Color, private val intensity: Double) : Material {

    override fun scatter(ray: Ray, hit: Hit): Scattered? {
        // Emissive material does not scatter light, it emits light
        return null
    }

    override fun emitted(u: Double, v: Double, p: Point3): Color {
        // Return the color that the material emits, scaled by the intensity
        return emitColor * intensity
    }
}