package raytracer.model

class RayTracer(private val world: World, var maxDepth: Int) {
    fun color(ray: Ray): Color {
        return colorRecursively(ray, maxDepth)
    }

    private fun colorRecursively(ray: Ray, depth: Int): Color {
        if (depth <= 0) return Point3.ZERO

        val hitRecord = world.hit(ray, 0.001, Double.POSITIVE_INFINITY)
        return if (hitRecord != null) {
            val scattered = hitRecord.material.scatter(ray, hitRecord)
            if (scattered != null) {
                scattered.attenuation * colorRecursively(scattered.ray, depth - 1)
            } else {
                Point3.ZERO
            }
        } else {
            val unitDirection = ray.direction.unit()
            val t = 0.5 * (unitDirection.y + 1.0)
            Color(1.0, 1.0, 1.0) * (1.0 - t) + Color(0.5, 0.7, 1.0) * t
        }
    }

    companion object {
        fun default(): RayTracer = RayTracer(World.default(), 7)
    }
}