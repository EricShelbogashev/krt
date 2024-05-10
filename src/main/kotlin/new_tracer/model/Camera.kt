package new_tracer.model

import new_tracer.algebra.Point3
import new_tracer.algebra.times
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan
import kotlin.random.Random

/**
 * Класс камеры для управления параметрами камеры и генерации лучей.
 */
data class Camera(var settings: Settings) {
    /**
     * Перечисление направлений движения камеры.
     */
    enum class Direction {
        LEFT, RIGHT, UP, DOWN, FORWARD, BACKWARD
    }

    /**
     * Внутренний класс настроек камеры, содержащий основные параметры визуализации и камеры.
     */
    data class Settings(
        var origin: Point3,  // начальная точка камеры
        var target: Point3,  // точка, на которую камера направлена
        val vup: Point3,     // вектор вертикального восходящего направления
        val vFov: Double,    // вертикальное поле зрения в градусах
        val aspectRatio: Double, // соотношение сторон
        val aperture: Double,    // диафрагма
        val focusDistance: Double, // расстояние фокусировки
        val time0: Double,   // начальное время
        val time1: Double    // конечное время
    ) {
        lateinit var w: Point3  // вектор направления взгляда камеры
        lateinit var u: Point3  // вектор, ортогональный к w и vup
        lateinit var v: Point3  // вектор, ортогональный к w и u
        lateinit var horizontal: Point3  // горизонтальный вектор в плоскости камеры
        lateinit var vertical: Point3    // вертикальный вектор в плоскости камеры
        lateinit var lowerLeftCorner: Point3  // нижний левый угол видового порта
        val lensRadius = aperture / 2.0

        /**
         * Пересчитывает все зависимые от параметров значения при изменении настроек камеры.
         */
        fun recalculate() {
            val theta = Math.toRadians(vFov)
            val h = tan(theta / 2)
            val viewPortHeight = 2.0 * h
            val viewPortWidth = aspectRatio * viewPortHeight
            w = (origin - target).unit()
            u = vup.cross(w).unit()
            v = w.cross(u)
            horizontal = focusDistance * viewPortWidth * u
            vertical = focusDistance * viewPortHeight * v
            lowerLeftCorner = origin - 0.5 * horizontal - 0.5 * vertical - focusDistance * w
        }
    }

    /**
     * Генерирует луч на основе параметров s и t, которые задают позицию на видовом порте.
     */
    fun ray(s: Double, t: Double): Ray {
        val rd = settings.lensRadius * Point3.randomInUnitDisk()
        val offset = settings.u * rd.x + settings.v * rd.y
        return Ray(
            origin = settings.origin + offset,
            direction = settings.lowerLeftCorner + s * settings.horizontal + t * settings.vertical - settings.origin - offset,
            time = Random.nextDouble(settings.time0, settings.time1)
        )
    }

    /**
     * Вращает камеру вокруг её начальной точки с использованием углов Yaw и Pitch.
     */
    fun rotate(yaw: Double, pitch: Double) {
        val forward = Point3(cos(pitch) * cos(yaw), sin(pitch), cos(pitch) * sin(yaw))
        settings.target = settings.origin + forward
        settings.recalculate()
    }

    /**
     * Перемещает камеру в указанном направлении на указанное расстояние.
     */
    fun move(direction: Direction, speed: Double) {
        when (direction) {
            Direction.LEFT -> moveTo(-settings.u, speed)
            Direction.RIGHT -> moveTo(settings.u, speed)
            Direction.UP -> moveTo(settings.v, speed)
            Direction.DOWN -> moveTo(-settings.v, speed)
            Direction.FORWARD -> moveTo(-settings.w, speed)
            Direction.BACKWARD -> moveTo(settings.w, speed)
        }
        settings.recalculate()
    }

    private fun moveTo(direction: Point3, speed: Double) {
        settings.origin += direction * speed
    }

    companion object {
        val default
            get() = Camera(
                settings = Settings(
                    origin = Point3.ONE,
                    target = Point3.ZERO,
                    vup = Point3(0, 1, 0),
                    vFov = 90.0,
                    aspectRatio = 16.0 / 9.0,
                    aperture = 0.1,
                    focusDistance = 10.0,
                    time0 = 0.0,
                    time1 = 1.0
                )
            )
    }
}
