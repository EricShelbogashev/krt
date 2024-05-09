package raytracer.view

import raytracer.model.Camera
import raytracer.model.ImagePanel
import raytracer.model.RayTracer
import raytracer.model.RenderController
import java.awt.BorderLayout
import java.awt.event.*
import java.awt.image.BufferedImage
import javax.swing.JFrame
import javax.swing.SwingUtilities


object Application : KeyListener, MouseListener, MouseMotionListener {
    private val rayTracer = RayTracer.default()
    private val cameraController = Camera.default()
    private val renderController = RenderController.default()
    private val renderedImage = BufferedImage(
        cameraController.imageWidth,
        cameraController.imageHeight,
        BufferedImage.TYPE_INT_ARGB
    )
    private var imagePanel = ImagePanel(renderedImage)

    private var prevX: Int = 0
    private var prevY: Int = 0

    @JvmStatic
    fun main(args: Array<String>) {
        SwingUtilities.invokeLater {
            JFrame("Ray Traced Image").apply {
                defaultCloseOperation = JFrame.EXIT_ON_CLOSE
                addKeyListener(this@Application)
                addMouseMotionListener(this@Application)
                addMouseListener(this@Application)
                layout = BorderLayout()
                contentPane.add(imagePanel, BorderLayout.CENTER)
                pack()
                setLocationRelativeTo(null)
                isVisible = true
                isFocusable = true
            }
        }
        renderImage()
    }

    private fun renderImage() {
//        renderController.renderImage(rayTracer, cameraController) {}
    }

    override fun mouseDragged(e: MouseEvent) {
        val deltaX = (e.x - prevX) * 2 * Math.PI / cameraController.imageWidth * 0.1
        val deltaY = (e.y - prevY) * 2 * Math.PI / cameraController.imageHeight * 0.1

        cameraController.adjustYaw(deltaX)
        cameraController.adjustPitch(-deltaY)
        prevX = e.x
        prevY = e.y
        renderImage()
    }

    override fun mousePressed(e: MouseEvent) {
        prevX = e.x
        prevY = e.y
    }

    override fun keyPressed(e: KeyEvent) {
        when (e.keyCode) {
            KeyEvent.VK_W, KeyEvent.VK_UP -> cameraController.moveForward()
            KeyEvent.VK_S, KeyEvent.VK_DOWN -> cameraController.moveBackward()
            KeyEvent.VK_A, KeyEvent.VK_LEFT -> cameraController.moveLeft()
            KeyEvent.VK_D, KeyEvent.VK_RIGHT -> cameraController.moveRight()
            KeyEvent.VK_R -> cameraController.moveUp()
            KeyEvent.VK_F -> cameraController.moveDown()
        }
        renderImage()
    }

    override fun keyReleased(e: KeyEvent?) {}
    override fun keyTyped(e: KeyEvent?) {}
    override fun mouseMoved(e: MouseEvent?) {}
    override fun mouseClicked(e: MouseEvent?) {}
    override fun mouseReleased(e: MouseEvent?) {}
    override fun mouseEntered(e: MouseEvent?) {}
    override fun mouseExited(e: MouseEvent?) {}
}
