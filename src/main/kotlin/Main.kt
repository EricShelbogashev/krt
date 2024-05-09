import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import raytracer.model.Camera
import raytracer.model.RayTracer
import raytracer.model.RenderController
import java.awt.image.BufferedImage

fun main() = application {
    val rayTracer = RayTracer.default()
    val camera = Camera.default()
    val renderController = RenderController.default()

    val renderedImage = BufferedImage(
        camera.imageWidth,
        camera.imageHeight,
        BufferedImage.TYPE_INT_ARGB
    )

    var imageBitmap by remember { mutableStateOf(renderedImage.toComposeImageBitmap()) }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Ray Tracing"
    ) {
        var prevX by remember { mutableStateOf(0) }
        var prevY by remember { mutableStateOf(0) }

        val focusRequester = remember { FocusRequester() }
        Box(modifier = Modifier.fillMaxSize().focusRequester(focusRequester).pointerInput(Unit) {
            detectDragGestures(onDragStart = { offset ->
                prevX = offset.x.toInt()
                prevY = offset.y.toInt()
            }, onDrag = { change, dragAmount ->
                val deltaX = (change.position.x.toInt() - prevX) * 2 * Math.PI / camera.imageWidth * 0.1
                val deltaY = (change.position.y.toInt() - prevY) * 2 * Math.PI / camera.imageHeight * 0.1

                camera.adjustYaw(deltaX)
                camera.adjustPitch(-deltaY)
                prevX = change.position.x.toInt()
                prevY = change.position.y.toInt()
                change.consume()
                renderController.renderImage(renderedImage, rayTracer, camera) {
                    imageBitmap = renderedImage.toComposeImageBitmap()
                }
            })
        }.focusable().onKeyEvent { keyEvent ->
            if (keyEvent.type == KeyEventType.KeyDown) {
                handleKeyEvent(keyEvent, camera)
                renderController.renderImage(renderedImage, rayTracer, camera) {
                    imageBitmap = renderedImage.toComposeImageBitmap()
                }
                true
            } else false
        }) {
            Image(bitmap = imageBitmap, contentDescription = null, modifier = Modifier.align(Alignment.Center))
        }
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}

fun handleKeyEvent(keyEvent: KeyEvent, cameraController: Camera) {
    when (keyEvent.key) {
        Key.W, Key.DirectionUp -> cameraController.moveForward()
        Key.S, Key.DirectionDown -> cameraController.moveBackward()
        Key.A, Key.DirectionLeft -> cameraController.moveLeft()
        Key.D, Key.DirectionRight -> cameraController.moveRight()
        Key.R -> cameraController.moveUp()
        Key.F -> cameraController.moveDown()
    }
}