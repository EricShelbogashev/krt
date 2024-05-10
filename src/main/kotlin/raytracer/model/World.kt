package raytracer.model

import kotlin.random.Random

class World(
    private var root: Traceable? = null
) {
    fun translateObjectById(objectId: String, offset: Point3) {
        translateById(root, objectId, offset)
    }

    private fun translateById(node: Traceable?, objectId: String, offset: Point3) {
        when (node) {
            is BVHNode -> {
                node.left?.let { translateById(it, objectId, offset) }
                node.right?.let { translateById(it, objectId, offset) }
            }

            is Sphere -> {
                if (node.id() == objectId) node.translate(offset)
            }
        }
    }

    fun getObjectIds(): List<String> = collectIds(root)

    private fun collectIds(node: Traceable?): List<String> {
        return when (node) {
            is BVHNode -> {
                val leftIds = node.left?.let { collectIds(it) } ?: emptyList()
                val rightIds = node.right?.let { collectIds(it) } ?: emptyList()
                leftIds + rightIds
            }

            is Sphere -> listOf(node.id())
            else -> emptyList()
        }
    }

    fun add(obj: Traceable) {
        root = if (root == null) {
            obj
        } else {
            BVHNode(listOf(root!!, obj), 0.0, 1.0) // Assuming fixed time0 and time1 for simplicity
        }
    }

    fun hit(ray: Ray, tMin: Double, tMax: Double): Hit? {
        return root?.hit(ray, tMin, tMax)
    }

    companion object {
        fun default(): World {
            val world = World()
            world.add(Sphere(Point3(0.0, -1000.0, 0.0), 1000.0, Lambert(Color(0.5, 0.5, 0.5))))

            for (a in -11 until 11) {
                for (b in -11 until 11) {
                    val chooseMaterial = Random.nextDouble()
                    val center = Point3(a + 0.9 * Random.nextDouble(), 0.2, b + 0.9 * Random.nextDouble())
                    if ((center - Point3(4.0, 0.2, 0.0)).length() > 0.9) {
                        val material: Material = when {
                            chooseMaterial < 0.8 -> Lambert(Point3.random() * Point3.random())
                            chooseMaterial < 0.95 -> Metal(Point3.random(0.5, 1.0), Random.nextDouble(0.0, 0.5))
                            else -> Dielectric(1.5)
                        }
                        world.add(Sphere(center, 0.2, material))
                    }
                }
            }

            world.add(Sphere(Point3(0.0, 1.0, 0.0), 1.0, Dielectric(1.5)))
            val obj = object : Sphere(Point3(-4.0, 1.0, 0.0), 2.0, Lambert(Color(0.4, 0.2, 0.1))) {
                override fun id(): String {
                    return "TEST"
                }
            }
            world.add(obj)
            world.add(Sphere(Point3(4.0, 1.0, 0.0), 1.0, Metal(Color(0.7, 0.6, 0.5), 0.05)))

            return world
        }
    }
}


class BVHNode(objects: List<Traceable>, time0: Double, time1: Double) : Traceable {
    var left: Traceable? = null
    var right: Traceable? = null
    private var box: AABB? = null

    init {
        val axis = Random.nextInt(3)
        val sortedObjects = objects.sortedWith { a, b ->
            a.boundingBox(time0, time1)!!.min[axis].compareTo(b.boundingBox(time0, time1)!!.min[axis])
        }
        when (sortedObjects.size) {
            1 -> {
                left = sortedObjects[0]
                right = sortedObjects[0]
            }

            2 -> {
                left = sortedObjects[0]
                right = sortedObjects[1]
            }

            else -> {
                val mid = sortedObjects.size / 2
                left = BVHNode(sortedObjects.subList(0, mid), time0, time1)
                right = BVHNode(sortedObjects.subList(mid, sortedObjects.size), time0, time1)
            }
        }

        val leftBox = left!!.boundingBox(time0, time1)!!
        val rightBox = right!!.boundingBox(time0, time1)!!
        box = AABB.surroundingBox(leftBox, rightBox)
    }

    override fun hit(ray: Ray, tMin: Double, tMax: Double): Hit? {
        if (!box?.hit(ray, tMin, tMax)!!) return null

        val leftHit = left?.hit(ray, tMin, tMax)
        val rightHit = right?.hit(ray, tMin, if (leftHit != null) minOf(leftHit.t, tMax) else tMax)

        return when {
            rightHit != null && (leftHit == null || rightHit.t < leftHit.t) -> rightHit
            leftHit != null -> leftHit
            else -> null
        }
    }

    override fun boundingBox(time0: Double, time1: Double): AABB? {
        return box
    }

    override fun translate(offset: Point3) {
        left?.translate(offset)
        right?.translate(offset)
    }
}

class AABB(val min: Point3, val max: Point3) {

    fun hit(ray: Ray, tMin: Double, tMax: Double): Boolean {
        for (a in 0 until 3) {
            val invD = 1.0 / ray.direction[a]
            var t0 = (min[a] - ray.origin[a]) * invD
            var t1 = (max[a] - ray.origin[a]) * invD
            if (invD < 0.0) {
                val temp = t0
                t0 = t1
                t1 = temp
            }
            val newTMin = if (t0 > tMin) t0 else tMin
            val newTMax = if (t1 < tMax) t1 else tMax
            if (newTMax <= newTMin) return false
        }
        return true
    }

    companion object {
        fun surroundingBox(box0: AABB, box1: AABB): AABB {
            val small = Point3(
                minOf(box0.min.x, box1.min.x), minOf(box0.min.y, box1.min.y), minOf(box0.min.z, box1.min.z)
            )
            val big = Point3(
                maxOf(box0.max.x, box1.max.x), maxOf(box0.max.y, box1.max.y), maxOf(box0.max.z, box1.max.z)
            )
            return AABB(small, big)
        }
    }
}
