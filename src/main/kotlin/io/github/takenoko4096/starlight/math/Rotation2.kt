package io.github.takenoko4096.starlight.math

import java.util.Objects
import kotlin.math.*

class Rotation2(var yaw: Float, var pitch: Float) : IVector<Rotation2, Float> {
    override val isZero
        get() = equals(Rotation2())

    constructor() : this(0f, 0f)

    override fun components(): List<Float> {
        return listOf(yaw, pitch)
    }

    override operator fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (other !is Rotation2) return false
        return yaw == other.yaw && pitch == other.pitch
    }

    override fun hashCode(): Int {
        return Objects.hash(yaw, pitch)
    }

    @Destructive
    override fun calculate(operator: (Float) -> Float): Rotation2 {
        yaw = operator(yaw)
        pitch = operator(pitch)
        return this
    }

    @Destructive
    override fun calculate(other: Rotation2, operator: (Float, Float) -> Float): Rotation2 {
        yaw = operator(yaw, other.yaw)
        pitch = operator(pitch, other.pitch)
        return this
    }

    @Destructive
    override fun calculate(other1: Rotation2, other2: Rotation2, operator: (Float, Float, Float) -> Float): Rotation2 {
        this.yaw = operator(yaw, other1.yaw, other2.yaw)
        this.pitch = operator(pitch, other1.pitch, other2.pitch)
        return this
    }

    @Destructive
    override infix fun add(other: Rotation2): Rotation2 {
        return calculate(other) { a, b -> a + b }
    }

    @Destructive
    override infix fun subtract(other: Rotation2): Rotation2 {
        return calculate(other) { a, b -> a - b }
    }

    @Destructive
    override infix fun scale(scalar: Float): Rotation2 {
        return calculate { component -> component * scalar }
    }

    @Destructive
    override fun invert(): Rotation2 {
        yaw += 180f
        pitch *= -1f
        return this
    }

    @Destructive
    override fun clamp(min: Rotation2, max: Rotation2): Rotation2 {
        return calculate(min, max) { value, minValue, maxValue ->
            max(minValue, min(value, maxValue))
        }
    }

    fun format(pattern: String, digits: Int): String {
        val format = "%.${digits}f"

        val sy = String.format(format, yaw)
        val sp = String.format(format, pitch)

        return pattern
            .replace("#yaw".toRegex(), sy)
            .replace("#pitch".toRegex(), sp)
    }

    override fun toString(): String {
        return format("(#yaw, #pitch)", 2)
    }

    override fun copy(): Rotation2 {
        return Rotation2(yaw, pitch)
    }

    infix fun angleBetween(other: Rotation2): Double {
        return toDirection3d().angleBetween(other.toDirection3d())
    }

    fun toDirection3d(): Vector3 {
        return Vector3(
            -sin(yaw * PI / 180) * cos(pitch * PI / 180),
            -sin(pitch * PI / 180),
            cos(yaw * PI / 180) * cos(pitch * PI / 180)
        )
    }

    companion object {
        val north
            get() = Rotation2(180f, 0f)

        val south
            get() = Rotation2(0f, 0f)

        val east
            get() = Rotation2(-90f, 0f)

        val west
            get() = Rotation2(90f, 0f)

        val up
            get() = Rotation2(0f, -90f)

        val down
            get() = Rotation2(0f, 90f)
    }
}
