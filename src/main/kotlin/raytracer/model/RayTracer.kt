package raytracer.model

class RayTracer(val world: World, var maxDepth: Int) {
    fun color(ray: Ray): Color {
        return colorRecursively(ray, maxDepth)
    }

    private fun colorRecursively(ray: Ray, depth: Int): Color {
        if (depth <= 0) return Point3.ZERO

        val hitRecord = world.hit(ray, 0.001, Double.POSITIVE_INFINITY)
        return if (hitRecord != null) {
            val scattered = hitRecord.material.scatter(ray, hitRecord)
            val emitted = hitRecord.material.emitted(0.0, 0.0, hitRecord.p) // u and v are set to 0.0 for simplicity
            if (scattered != null) {
                emitted + scattered.attenuation * colorRecursively(scattered.ray, depth - 1)
            } else {
                emitted
            }
        } else {
            val unitDirection = ray.direction.unit()
            val t = 0.5 * (unitDirection.y + 1.0)
            Color(1.0, 1.0, 1.0) * (1.0 - t) + Color(0.5, 0.7, 1.0) * t
        }
    }
}