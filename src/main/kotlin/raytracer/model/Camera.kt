package raytracer.model

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

/**
 * Класс, представляющий камеру в трёхмерном пространстве.
 * Камера определяется её положением, направлением взгляда и настройками проекции.
 *
 * @param origin Точка, из которой смотрит камера.
 * @param target Точка, на которую смотрит камера.
 * @param vup Вектор, направленный вверх от камеры.
 * @param fov Вертикальный угол обзора камеры в градусах.
 * @param aperture  Диаметр апертуры камеры.
 * @param focusDist  Фокусное расстояние от камеры до объекта съёмки.
 */
class Camera(
    private var origin: Point3,
    private var target: Point3,
    vup: Point3,
    fov: Double,
    var aperture: Double,
    focusDist: Double,
    var imageWidth: Int,
    var imageHeight: Int,
    samplesPerPixel: Int,
) {
    private var _origin = origin
    private var _target = target
    private var _vup = vup
    private var _imageWidth = imageWidth.toDouble()
    private var _imageHeight = imageHeight.toDouble()

    var vup: Point3 = _vup
        set(value) {
            field = value
            updateCamera()
        }
    var fov: Double = fov
        set(value) {
            field = value
            updateProjection()
        }
    var focusDist: Double = focusDist
        set(value) {
            field = value
            updateProjection()
        }
    var samplesPerPixel: Int = 5
    val aspectRatio: Double get() = _imageWidth / _imageHeight
    val lensRadius: Double get() = aperture / 2
    var movementSpeed: Double = 0.1

    private var v: Point3 = Point3.ZERO
    private var u: Point3 = Point3.ZERO
    private var w: Point3 = Point3.ZERO
    private var lowerLeftCorner: Point3 = Point3.ZERO
    private var horizontal: Point3 = Point3.ZERO
    private var vertical: Point3 = Point3.ZERO

    private var yaw = 0.0
    private var pitch = 0.0
    private val maxPitch = Math.PI / 2 - 0.01

    init {
        updateCamera()
    }

    fun adjustYaw(delta: Double) {
        yaw += delta
        updateCamera()
    }

    fun adjustPitch(delta: Double) {
        pitch += delta
        pitch = pitch.coerceIn(-maxPitch, maxPitch)
        updateCamera()
    }

    fun moveForward() {
        moveInDirection(cos(pitch) * cos(yaw), sin(pitch), cos(pitch) * sin(yaw))
    }

    fun moveBackward() {
        moveInDirection(-cos(pitch) * cos(yaw), -sin(pitch), -cos(pitch) * sin(yaw))
    }

    fun moveLeft() {
        moveInDirection(sin(yaw), 0.0, -cos(yaw))
    }

    fun moveRight() {
        moveInDirection(-sin(yaw), 0.0, cos(yaw))
    }

    fun moveUp() {
        _origin += _vup * movementSpeed
        updateCamera()
    }

    fun moveDown() {
        _origin -= _vup * movementSpeed
        updateCamera()
    }

    private fun moveInDirection(x: Double, y: Double, z: Double) {
        val direction = Point3(x, y, z)
        _origin += direction * movementSpeed
        updateCamera()
    }

    private fun updateCamera() {
        updateCameraLookAt()
        updateOrientation()
        updateProjection()
    }

    private fun updateCameraLookAt() {
        val forward = Point3(cos(pitch) * cos(yaw), sin(pitch), cos(pitch) * sin(yaw))
        _target = _origin + forward
    }

    private fun updateOrientation() {
        w = (_origin - _target).unit()
        u = _vup.cross(w).unit()
        v = w.cross(u)
    }

    private fun updateProjection() {
        val theta = Math.toRadians(fov)
        val halfHeight = tan(theta / 2)
        val halfWidth = aspectRatio * halfHeight

        lowerLeftCorner = origin - u * halfWidth * focusDist - v * halfHeight * focusDist - w * focusDist
        horizontal = u * 2.0 * halfWidth * focusDist
        vertical = v * 2.0 * halfHeight * focusDist
    }

    fun ray(s: Double, t: Double): Ray {
        val rd = Point3.randomInUnitDisk() * lensRadius
        val offset = u * rd.x + v * rd.y
        return Ray(
            _origin + offset,
            lowerLeftCorner + horizontal * s + vertical * t - _origin - offset
        )
    }

    companion object {
        fun default() = Camera(
            origin = Point3.ONE,
            target = Point3.ZERO,
            vup = Point3(0.0, 1.0, 0.0),
            fov = 90.0,
            aperture = 0.1,
            focusDist = 10.0,
            imageWidth = 400,
            imageHeight = 400,
            samplesPerPixel = 5,
        )
    }
}
