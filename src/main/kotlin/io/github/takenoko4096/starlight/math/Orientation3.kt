package io.github.takenoko4096.starlight.math

import org.joml.Quaternionf
import java.util.Objects
import kotlin.math.*

class Orientation3(var yaw: Float, var pitch: Float, var roll: Float) : IVector<Orientation3, Float> {
    override val isZero: Boolean
        get() = equals(Orientation3())

    val objectsCoordsSystem: ObjectCoordsSystem
        get() = ObjectCoordsSystem(this)

    constructor() : this(0f, 0f, 0f)

    override fun components(): List<Float> {
        return listOf(yaw, pitch, roll)
    }

    override operator fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (other !is Orientation3) return false
        return yaw == other.yaw && pitch == other.pitch && roll == other.roll
    }

    override fun hashCode(): Int {
        return Objects.hash(yaw, pitch, roll)
    }

    @Destructive
    override fun calculate(operator: (Float) -> Float): Orientation3 {
        yaw = operator(yaw)
        pitch = operator(pitch)
        roll = operator(roll)
        return this
    }

    @Destructive
    override fun calculate(other: Orientation3, operator: (Float, Float) -> Float): Orientation3 {
        yaw = operator(yaw, other.yaw)
        pitch = operator(pitch, other.pitch)
        roll = operator(roll, other.roll)
        return this
    }

    @Destructive
    override fun calculate(other1: Orientation3, other2: Orientation3, operator: (Float, Float, Float) -> Float): Orientation3 {
        yaw = operator(yaw, other1.yaw, other2.yaw)
        pitch = operator(pitch, other1.pitch, other2.pitch)
        roll = operator(roll, other1.roll, other2.roll)
        return this
    }

    @Destructive
    override fun add(other: Orientation3): Orientation3 {
        return calculate(other) { a, b -> a + b }
    }

    @Destructive
    override fun subtract(other: Orientation3): Orientation3 {
        return calculate(other) { a, b -> a - b }
    }

    @Destructive
    override fun scale(scalar: Float): Orientation3 {
        return calculate { component -> component * scalar }
    }

    override fun invert(): Orientation3 {
        return this.objectsCoordsSystem.back()
    }

    @Destructive
    override fun clamp(min: Orientation3, max: Orientation3): Orientation3 {
        return calculate(min, max) { value, minValue, maxValue ->
            max(minValue, min(value, maxValue))
        }
    }

    fun format(pattern: String, digits: Int): String {
        val format = "%.${digits}f"

        val sy = String.format(format, yaw)
        val sp = String.format(format, pitch)
        val sr = String.format(format, roll)

        return pattern
            .replace("#yaw".toRegex(), sy)
            .replace("#pitch".toRegex(), sp)
            .replace("#roll".toRegex(), sr)
    }

    override fun toString(): String {
        return format("(#yaw, #pitch, #roll)", 2)
    }

    override fun copy(): Orientation3 {
        return Orientation3(yaw, pitch, roll)
    }

    fun toRotation2f(): Rotation2 {
        return Rotation2(yaw, pitch)
    }

    fun toDirection3d(): Vector3 {
        return toRotation2f().toDirection3d()
    }

    fun toQuaternion4f(): Quaternionf {
        val quaternion = Quaternionf(0f, 0f, 0f, 1f)
        val system = objectsCoordsSystem

        fun rotate(axis: Vector3, angle: Float) {
            val normalized: Vector3 = axis.copy().normalize()
            quaternion.rotateAxis(
                (angle * PI / 180).toFloat(),
                normalized.x.toFloat(),
                normalized.y.toFloat(),
                normalized.z.toFloat()
            )
        }

        rotate(system.z, roll)
        rotate(system.x, pitch)
        rotate(Vector3.up, -(yaw + 90))
        return quaternion
    }

    class ObjectCoordsSystem internal constructor(rotation: Orientation3) {
        private val rotation: Orientation3 = rotation.copy()

        val x: Vector3
            get() {
                val forward = z
                return Vector3(forward.z, 0.0, -forward.x).normalize().rotate(forward, rotation.roll)
            }

        val y: Vector3
            get() = z.cross(x)

        val z: Vector3
            get() = rotation.toDirection3d()

        fun forward(): Orientation3 {
            return rotation.copy()
        }

        fun back(): Orientation3 {
            return ofAxes(x.invert(), y)
        }

        fun left(): Orientation3 {
            return ofAxes(z.invert(), y)
        }

        fun right(): Orientation3 {
            return ofAxes(z, y)
        }

        fun up(): Orientation3 {
            return ofAxes(x, z.invert())
        }

        fun down(): Orientation3 {
            return ofAxes(x, z)
        }

        private fun ofAxes(x: Vector3, y: Vector3): Orientation3 {
            val z = x.cross(y)

            return Orientation3(
                (atan2(-z.x, z.z) * 180.0 / PI).toFloat(),
                (asin(-z.y) * 180 / PI).toFloat(),
                (atan2(x.y, y.y) * 180.0 / PI).toFloat()
            )
        }
    }

    companion object {
        fun from(other: Rotation2): Orientation3 {
            return Orientation3(other.yaw, other.pitch, 0f)
        }
    }
}
