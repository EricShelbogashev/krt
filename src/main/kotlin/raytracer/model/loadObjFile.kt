package raytracer.model

import java.io.File

fun loadObjFile(filePath: String, material: Material): List<Triangle> {
    val vertices = mutableListOf<Point3>()
    val triangles = mutableListOf<Triangle>()

    File(filePath).forEachLine { line ->
        if (line.startsWith("v ")) {
            val parts = line.split(" ").filter { it.isNotEmpty() }
            val x = parts[1].toDouble()
            val y = parts[2].toDouble()
            val z = parts[3].toDouble()
            vertices.add(Point3(x, y, z))
        } else if (line.startsWith("f ")) {
            val parts = line.split(" ").filter { it.isNotEmpty() }
            val v0 = vertices[parts[1].toInt() - 1]
            val v1 = vertices[parts[2].toInt() - 1]
            val v2 = vertices[parts[3].toInt() - 1]
            triangles.add(Triangle(v0, v1, v2, material))
        }
    }

    return triangles
}