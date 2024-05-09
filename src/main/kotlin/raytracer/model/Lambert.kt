package raytracer.model

class Lambert(private val albedo: Color) : Material {
    override fun scatter(ray: Ray, hit: Hit): Scattered {
        val scatterDirection = (hit.normal + Point3.randomInUnitSphere()).takeIf { !it.isNearZero() } ?: hit.normal
        val scatteredRay = Ray(hit.p, scatterDirection)
        val attenuation = albedo
        return Scattered(scatteredRay, attenuation)
    }
}
