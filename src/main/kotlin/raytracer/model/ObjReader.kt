package raytracer.model

import java.io.File
import kotlin.math.abs

class ObjParser(filePath: String, material: Material) {
    private val vertices = mutableListOf<Point3>()
    private val normals = mutableListOf<Point3>()
    val triangles = mutableListOf<Triangle>()

    init {
        parseFile(filePath, material)
        normalizeVertices()
    }

    private fun parseFile(filePath: String, material: Material) {
        File(filePath).forEachLine { line ->
            when {
                line.startsWith("v ") -> {
                    val parts = line.substring(2).split(" ").map { it.trim() }
                    vertices.add(Point3(parts[1].toDouble(), parts[2].toDouble(), parts[3].toDouble()))
                }

                line.startsWith("vn ") -> {
                    val parts = line.substring(2).split(" ").map { it.trim() }
                    normals.add(Point3(parts[1].toDouble(), parts[2].toDouble(), parts[3].toDouble()))
                }

                line.startsWith("f ") -> {
                    val parts = line.substring(2).split(" ").map { it.trim() }
                    if (parts.size == 5) { // Обработка четырехугольников
                        val idxGroups = parts.dropLast(1).map { it.split('/').map { it.toInt() - 1 } }
                        // Создаем два треугольника из квада
                        triangles.add(
                            Triangle(
                                vertices[idxGroups[0][0]],
                                vertices[idxGroups[1][0]],
                                vertices[idxGroups[2][0]],
                                material
                            )
                        )
                        triangles.add(
                            Triangle(
                                vertices[idxGroups[0][0]],
                                vertices[idxGroups[2][0]],
                                vertices[idxGroups[3][0]],
                                material
                            )
                        )
                    }
                }
            }
        }
    }

    private fun normalizeVertices() {
        if (vertices.isEmpty()) return
        val maxCoordinate = vertices.fold(0.0) { acc, vertex ->
            maxOf(acc, abs(vertex.x), abs(vertex.y), abs(vertex.z))
        }
        if (maxCoordinate == 0.0) return // To avoid division by zero if all vertices are at the origin.
        vertices.forEach { it /= maxCoordinate }
    }
}