import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import raytracer.model.Camera
import raytracer.model.RayTracer
import raytracer.model.RenderController
import raytracer.view.CameraSettings
import raytracer.view.RayTracerSettings
import raytracer.view.RenderControllerSettings
import java.awt.image.BufferedImage

fun main() = application {
    val rayTracer = RayTracer.default()
    val camera = Camera.default()
    val renderController = RenderController.default()

    var buffer = remember {
        BufferedImage(
            camera.imageWidth,
            camera.imageHeight,
            BufferedImage.TYPE_INT_ARGB
        )
    }

    var imageBitmap by remember { mutableStateOf(buffer.toComposeImageBitmap()) }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Ray Tracing"
    ) {
        val focusRequester = remember { FocusRequester() }
        var prevX by remember { mutableStateOf(0) }
        var prevY by remember { mutableStateOf(0) }

        Column(modifier = Modifier.fillMaxSize().focusRequester(focusRequester).pointerInput(Unit) {
            detectDragGestures(onDragStart = { offset ->
                prevX = offset.x.toInt()
                prevY = offset.y.toInt()
            }, onDrag = { change, _ ->
                val deltaX = (change.position.x.toInt() - prevX) * 2 * Math.PI / camera.imageWidth * 0.1
                val deltaY = (change.position.y.toInt() - prevY) * 2 * Math.PI / camera.imageHeight * 0.1

                camera.adjustYaw(deltaX)
                camera.adjustPitch(-deltaY)
                prevX = change.position.x.toInt()
                prevY = change.position.y.toInt()
                change.consume()

                renderController.renderImage(buffer, rayTracer, camera) {
                    imageBitmap = buffer.toComposeImageBitmap()
                }
            })
        }.focusable().onKeyEvent {
            if (it.type == KeyEventType.KeyDown) {
                handleKeyEvent(it, camera)
                renderController.renderImage(buffer, rayTracer, camera) {
                    imageBitmap = buffer.toComposeImageBitmap()
                }
                true
            } else false
        }) {
            Image(bitmap = imageBitmap, contentDescription = null)
            settingsPanel(rayTracer, camera, renderController, buffer, { buffer = it }) { newImage ->
                imageBitmap = newImage.toComposeImageBitmap()
            }
        }
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}

@Composable
fun settingsPanel(
    rayTracer: RayTracer,
    camera: Camera,
    renderController: RenderController,
    buffer: BufferedImage,
    updateBuffer: (BufferedImage) -> Unit,
    updateImage: (BufferedImage) -> Unit
) {
    Column {
        RayTracerSettings(rayTracer) {
            renderController.renderImage(buffer, rayTracer, camera) {
                updateImage(buffer)
            }
        }
        RenderControllerSettings(renderController) {
            renderController.renderImage(buffer, rayTracer, camera) {
                updateImage(buffer)
            }
        }
        CameraSettings(camera) {
            if (camera.imageWidth > 0 && camera.imageHeight > 0) {
                updateBuffer(
                    BufferedImage(
                        camera.imageWidth,
                        camera.imageHeight,
                        BufferedImage.TYPE_INT_ARGB
                    )
                )
                renderController.renderImage(buffer, rayTracer, camera) {
                    updateImage(buffer)
                }
            }
        }
    }
}

fun handleKeyEvent(keyEvent: KeyEvent, camera: Camera) {
    when (keyEvent.key) {
        Key.W, Key.DirectionUp -> camera.moveForward()
        Key.S, Key.DirectionDown -> camera.moveBackward()
        Key.A, Key.DirectionLeft -> camera.moveLeft()
        Key.D, Key.DirectionRight -> camera.moveRight()
        Key.R -> camera.moveUp()
        Key.F -> camera.moveDown()
    }
}
