package io.github.takenoko4096.starlight.math

import java.util.Objects
import kotlin.math.*

class BoundingPlane internal constructor(
    private val _center: Vector3d,
    private val _rotation: Orientation3f,
    val width: Double,
    val height: Double
) {
    val center: Vector3d
        get() = _center

    val rotation: Orientation3f
        get() = _rotation

    val normal: Vector3d
        get() = rotation.toDirection3d()

    val corners
        get() = Corners(this)

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (other !is BoundingPlane) return false
        return center == other.center && rotation == other.rotation && width == other.width && height == other.height
    }

    override fun hashCode(): Int {
        return Objects.hash(center, rotation, width, height)
    }

    fun rayCast(from: Vector3d, to: Vector3d): Vector3d? {
        val v1 = center deltaTo from
        val v2 = center deltaTo to

        if ((v1 dot normal) * (v2 dot normal) > 0) return null

        val system = rotation.localCoordinateSystem

        val vx = system.x.apply { length = width / 2 }
        val vy = system.y.apply { length = height / 2 }

        val rightDown = corners.rightDown
        val leftDown = corners.leftDown
        val rightUp = corners.rightUp
        val leftUp = corners.leftUp

        val beginAndEdges = mapOf(
            rightDown to (rightDown deltaTo rightUp),
            rightUp to (rightUp deltaTo leftUp),
            leftUp to (leftUp deltaTo leftDown),
            leftDown to (leftDown deltaTo rightDown)
        )

        val vA = rightDown deltaTo from
        val vB = rightDown deltaTo to
        val d1 = distanceBetween(from)
        val d2 = distanceBetween(to)
        val a = d1 / (d1 + d2)
        val vC = vA.scale(1 - a).add(vB.scale(a))
        val intersection = rightDown.copy().add(vC)

        val normals = mutableListOf<Vector3d>()

        for ((begin, edgeVector) in beginAndEdges.entries) {
            val p = begin deltaTo intersection
            normals.add((p cross edgeVector).normalize())
        }

        for (normalV in normals) {
            //TODO: !equals() -> !similar()
            if (normalV !== normal) {
                return null
            }
        }

        return intersection
    }

    fun distanceBetween(point: Vector3d): Double {
        return abs((center deltaTo point) dot normal)
    }

    class Corners internal constructor(plane: BoundingPlane) {
        val rightDown: Vector3d

        val leftDown: Vector3d

        val rightUp: Vector3d

        val leftUp: Vector3d

        init {
            val system = plane.rotation.localCoordinateSystem

            val vx = system.x.apply { length = plane.width / 2 }
            val vy = system.y.apply { length = plane.height / 2 }

            rightDown = plane.center.copy() subtract vx subtract vy
            leftDown = plane.center.copy() add vx subtract vy
            rightUp = plane.center.copy() subtract vx add vy
            leftUp = plane.center.copy() add vx add vy
        }
    }
}
