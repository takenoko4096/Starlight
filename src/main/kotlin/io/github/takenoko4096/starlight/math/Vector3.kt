package io.github.takenoko4096.starlight.math

import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3
import java.util.Objects
import kotlin.math.*

class Vector3(var x: Double, var y: Double, var z: Double) : IVector<Vector3, Double> {
    override val isZero
        get() = equals(Vector3())

    @Destructive
    var length
        get() = sqrt(dot(this))
        set(value) {
            normalize().scale(value)
        }

    constructor() : this(0.0, 0.0, 0.0)

    constructor(x: Int, y: Int, z: Int) : this(x.toDouble(), y.toDouble(), z.toDouble())

    override fun components(): List<Double> {
        return listOf(x, y, z)
    }

    override fun hashCode(): Int {
        return Objects.hash(x, y, z)
    }

    override operator fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other === this) return true
        if (other !is Vector3) return false
        return x == other.x && y == other.y && z == other.z
    }

    @Destructive
    override fun calculate(operator: (Double) -> Double): Vector3 {
        x = operator(x)
        y = operator(y)
        z = operator(z)
        return this
    }

    @Destructive
    override fun calculate(other: Vector3, operator: (Double, Double) -> Double): Vector3 {
        x = operator(x, other.x)
        y = operator(y, other.y)
        z = operator(z, other.z)
        return this
    }

    override fun calculate(other1: Vector3, other2: Vector3, operator: (Double, Double, Double) -> Double): Vector3 {
        x = operator(x, other1.x, other2.x)
        y = operator(y, other1.y, other2.y)
        z = operator(z, other1.z, other2.z)
        return this
    }

    @Destructive
    override infix fun add(other: Vector3): Vector3 {
        return calculate(other) { a, b -> a + b }
    }

    @Destructive
    override infix fun subtract(other: Vector3): Vector3 {
        return add(other.copy().invert())
    }

    @Destructive
    override infix fun scale(scalar: Double): Vector3 {
        return calculate { component -> component * scalar }
    }

    @Destructive
    override fun invert(): Vector3 {
        return scale(-1.0)
    }

    infix fun dot(other: Vector3): Double {
        return x * other.x + y * other.y + z * other.z
    }

    infix fun cross(other: Vector3): Vector3 {
        val x2 = other.x
        val y2 = other.y
        val z2 = other.z

        return Vector3(
            y * z2 - z * y2,
            z * x2 - x * z2,
            x * y2 - y * x2
        )
    }

    infix fun angleBetween(other: Vector3): Double {
        val p = this.dot(other) / (length * other.length)

        if (abs(p) > 1.0) {
            return 0.0
        }

        return acos(p) * 180 / PI
    }

    @Destructive
    fun normalize(): Vector3 {
        val l = length
        return calculate { component -> component / l }
    }

    infix fun directionTo(other: Vector3): Vector3 {
        if (this == other) {
            throw IllegalArgumentException("2つのベクトルは完全に一致しています")
        }

        return other.copy().subtract(this).normalize()
    }

    infix fun distanceBetween(other: Vector3): Double {
        return sqrt((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y) + (z - other.z) * (z - other.z))
    }

    infix fun projection(other: Vector3): Vector3 {
        return other.copy().scale(
            other.length * length / other.length * other.length
        )
    }

    infix fun rejection(other: Vector3): Vector3 {
        return copy().subtract(projection(other))
    }

    infix fun reflect(normal: Vector3): Vector3 {
        return this.copy().calculate(normal) { a, b -> a - 2 * dot(normal) * b }
    }

    fun lerp(end: Vector3, t: Float): Vector3 {
        return copy().calculate(end) { a, b -> (1 - t) * a + t * b }
    }

    fun slerp(end: Vector3, s: Float): Vector3 {
        val angle = this.angleBetween(end) * Math.PI / 180

        val p1 = sin(angle * (1 - s)) / sin(angle)
        val p2 = sin(angle * s) / sin(angle)

        val q1 = this.copy().scale(p1)
        val q2 = end.copy().scale(p2)

        return q1.add(q2)
    }

    @Destructive
    override fun clamp(min: Vector3, max: Vector3): Vector3 {
        return calculate(min, max) { value, minValue, maxValue ->
            max(minValue, min(value, maxValue))
        }
    }

    @Destructive
    fun rotate(axis: Vector3, degree: Float): Vector3 {
        val theta = Math.toRadians(degree.toDouble())
        val sin = sin(theta)
        val cos = cos(theta)
        val k = axis.copy().normalize()
        val v = copy()

        val r = v.copy().scale(cos)
            .add(k.cross(v).scale(sin))
            .add(k.copy().scale(k.dot(v) * (1 - cos)))

        x = r.x
        y = r.y
        z = r.z
        return this
    }

    fun format(pattern: String, digits: Int): String {
        val format = "%.${digits}f"

        val sx = String.format(format, x)
        val sy = String.format(format, y)
        val sz = String.format(format, z)

        return pattern
            .replace("#x".toRegex(), sx)
            .replace("#y".toRegex(), sy)
            .replace("#z".toRegex(), sz)
    }

    override fun toString(): String {
        return format("(#x, #y, #z)", 2)
    }

    override fun copy(): Vector3 {
        return Vector3(x, y, z)
    }

    fun toRotation2f(): Rotation2 {
        return Rotation2(
            (-atan2(x / length, z / length) * 180.0 / PI).toFloat(),
            (-asin(y / length) * 180.0 / PI).toFloat()
        )
    }

    fun toVec3(): Vec3 {
        return Vec3(x, y, z)
    }

    fun toBlockPos(floor: Boolean): BlockPos {
        return if (floor) BlockPos(floor(x).toInt(), floor(y).toInt(), floor(z).toInt())
        else BlockPos(x.toInt(), y.toInt(), z.toInt())
    }

    companion object {
        fun min(a: Vector3, b: Vector3): Vector3 {
            return a.copy().calculate(b) { a, b -> min(a, b) }
        }

        fun max(a: Vector3, b: Vector3): Vector3 {
            return a.copy().calculate(b) { a, b -> max(a, b) }
        }

        val forward
            get() = Vector3(0, 0, 1)

        val back
            get() = Vector3(0, 0, -1)

        val left
            get() = Vector3(1, 0, 0)

        val right
            get() = Vector3(-1, 0, 0)

        val up
            get() = Vector3(0, 1, 0)

        val down
            get() = Vector3(0, -1, 0)
    }
}