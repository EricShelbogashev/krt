package raytracer.model

import kotlinx.coroutines.*
import java.awt.Color
import java.awt.image.BufferedImage
import java.util.concurrent.Executors
import javax.swing.SwingUtilities
import kotlin.random.Random

class RenderController(
    var repaintIntervalMs: Long,
    var batchSize: Int,
) : AutoCloseable {
    private var renderJob: Job? = null
    private val renderDispatcher =
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()).asCoroutineDispatcher()
    private var lastRepaintTime = 0L
    private var useWireframe: Boolean = false

    fun toggleWireframeMode() {
        useWireframe = !useWireframe
    }

    fun renderImage(
        image: BufferedImage,
        rayTracer: RayTracer,
        camera: Camera,
        onUpdate: (() -> Unit),
    ) {
        renderJob?.cancel()
        if (useWireframe) {
            renderWireframe(image, rayTracer.world, camera, onUpdate)
        } else {
            renderRayTracedImage(image, rayTracer, camera, onUpdate)
        }

    }

    private fun renderRayTracedImage(
        image: BufferedImage,
        rayTracer: RayTracer,
        camera: Camera,
        onUpdate: (() -> Unit),
    ) {
        val pixels = (0 until camera.imageHeight).flatMap { j ->
            (0 until camera.imageWidth).map { i ->
                Pair(j, i)
            }
        }.shuffled()
        renderJob = CoroutineScope(renderDispatcher).launch {
            pixels.chunked(batchSize).forEach { batch ->
                batch.map { (j, i) ->
                    async {
                        val color = (0 until camera.samplesPerPixel).fold(Point3.ZERO) { acc, _ ->
                            val u = (i.toDouble() + Random.nextDouble()) / (camera.imageWidth - 1)
                            val v = (j.toDouble() + Random.nextDouble()) / (camera.imageHeight - 1)
                            val r = camera.ray(u, v)
                            acc + rayTracer.color(r)
                        }
                        if (isActive) {
                            image.setRGB(
                                i,
                                camera.imageHeight - 1 - j,
                                color.toAWTColor(camera.samplesPerPixel)
                            )
                        }
                    }
                }.awaitAll()

                val currentTime = System.currentTimeMillis()
                if (currentTime - lastRepaintTime > repaintIntervalMs) {
                    SwingUtilities.invokeLater {
                        onUpdate.invoke()
                    }
                    lastRepaintTime = currentTime
                }
            }
            onUpdate.invoke()
        }
    }

    private fun renderWireframe(
        image: BufferedImage,
        world: World,
        camera: Camera,
        onUpdate: (() -> Unit),
    ) {
        // Clear image
        val graphics = image.createGraphics()
        graphics.color = Color.BLACK
        graphics.fillRect(0, 0, image.width, image.height)
        graphics.dispose()

        // Render wireframe
        val lineSegments = world.objects.flatMap { it.linearize(1.0) } // Adjust the coefficient as needed
        val graphics2D = image.createGraphics()
        graphics2D.color = Color.WHITE

        lineSegments.forEach { segment ->
            val start = camera.project(segment.start)
            val end = camera.project(segment.end)
            if (start != null && end != null) {
                graphics2D.drawLine(start.x, start.y, end.x, end.y)
            }
        }

        graphics2D.dispose()
        onUpdate.invoke()
    }

    override fun close() {
        renderDispatcher.close()
    }

    companion object {
        fun default(): RenderController = RenderController(50L, 50)
    }
}