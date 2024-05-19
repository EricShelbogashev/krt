package raytracer.model

import kotlin.random.Random

class World(private val objects: MutableList<Traceable> = mutableListOf()) {

    fun translateObjectById(objectId: String, offset: Point3) {
        objects.find { it.id() == objectId }?.translate(offset)
    }

    fun getObjectIds(): List<String> = objects.filter { it.id() != null }.map { it.id()!! }

    fun add(obj: Traceable) {
        objects.add(obj)
    }

    fun hit(ray: Ray, tMin: Double, tMax: Double): Hit? {
        return objects.asSequence()
            .mapNotNull { it.hit(ray, tMin, tMax) }
            .minByOrNull { it.t }
    }

    companion object {
        fun default(): World {
            val world = World()
            world.add(Sphere(Point3(0.0, -1000.0, 0.0), 1000.0, Lambert(Color(0.5, 0.5, 0.5))))

            val random = Random.Default

            for (a in -11 until 11) {
                for (b in -11 until 11) {
                    val chooseMaterial = random.nextDouble()
                    val center = Point3(a + 0.9 * random.nextDouble(), 0.2, b + 0.9 * random.nextDouble())
                    if ((center - Point3(4.0, 0.2, 0.0)).length() > 0.9) {
                        val material: Material = when {
                            chooseMaterial < 0.8 -> Lambert(Point3.random() * Point3.random())
                            chooseMaterial < 0.95 -> Metal(Point3.random(0.5, 1.0), random.nextDouble(0.0, 0.5))
                            else -> Dielectric(1.5)
                        }
                        world.add(Sphere(center, 0.2, material))
                    }
                }
            }

            world.add(Sphere(Point3(0.0, 1.0, 0.0), 1.0, Dielectric(1.5)))
            world.add(object : Sphere(Point3(-4.0, 1.0, 0.0), 2.0, Lambert(Color(0.4, 0.2, 0.1))) {
                override fun id(): String {
                    return "TEST"
                }
            })
            world.add(Sphere(Point3(4.0, 1.0, 0.0), 1.0, Metal(Color(0.7, 0.6, 0.5), 0.05)))

            return world
        }
    }
}
