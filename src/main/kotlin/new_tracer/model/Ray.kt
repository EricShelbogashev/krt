package new_tracer.model

import new_tracer.algebra.Point3

/**
 * Класс для представления луча в трёхмерном пространстве.
 * Луч определяется начальной точкой, направлением и временем.
 */
class Ray(val origin: Point3, val direction: Point3, val time: Double) {
    /**
     * Вторичный конструктор, который позволяет создать луч без указания времени.
     * Время по умолчанию будет установлено в 0.0.
     *
     * @param origin Начальная точка луча.
     * @param direction Направление луча.
     */
    constructor(origin: Point3, direction: Point3) : this(origin, direction, 0.0)

    /**
     * Функция для получения точки на луче на заданном расстоянии t от начальной точки.
     *
     * @param t Параметр, указывающий расстояние вдоль луча от начальной точки.
     * @return Точка на луче, расположенная на расстоянии t от начальной точки.
     */
    fun at(t: Double): Point3 = origin + direction * t
}
