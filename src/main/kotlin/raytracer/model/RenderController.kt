package raytracer.model

import kotlinx.coroutines.*
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

    fun renderImage(
        image: BufferedImage,
        rayTracer: RayTracer,
        camera: Camera,
        onUpdate: (() -> Unit),
    ) {
        renderJob?.cancel()
        renderJob = CoroutineScope(renderDispatcher).launch {
            val pixels = (0 until camera.imageHeight).flatMap { j ->
                (0 until camera.imageWidth).map { i ->
                    Pair(j, i)
                }
            }.shuffled()
            pixels.chunked(batchSize).forEach { batch ->
                batch.map { (j, i) ->
                    async {
                        val color = (0 until camera.samplesPerPixel).fold(Point3.ZERO) { acc, _ ->
                            val u = (i.toDouble() + Random.nextDouble()) / (camera.imageWidth - 1)
                            val v = (j.toDouble() + Random.nextDouble()) / (camera.imageHeight - 1)
                            val r = camera.ray(u, v)
                            acc + rayTracer.color(r, rayTracer.maxDepth)
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

    override fun close() {
        renderDispatcher.close()
    }

    companion object {
        fun default(): RenderController = RenderController(50L, 50)
    }
}
