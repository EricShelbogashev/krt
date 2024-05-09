package raytracer.model

class Metal(private val albedo: Color, f: Double) : Material {
    private val fuzz: Double = if (f < 1) f else 1.0

    override fun scatter(ray: Ray, hit: Hit): Scattered? {
        val reflected = ray.direction.unit().reflect(hit.normal.unit())
        val scatterDirection = reflected + Point3.randomInUnitSphere() * fuzz
        val scatteredRay = Ray(hit.p, scatterDirection)
        val attenuation = albedo
        return if (scatterDirection.dot(hit.normal) > 0) {
            Scattered(scatteredRay, attenuation)
        } else {
            null
        }
    }
}
