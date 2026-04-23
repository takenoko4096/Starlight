package io.github.takenoko4096.starlight.execute

import io.github.takenoko4096.starlight.math.Vector3d
import net.minecraft.world.entity.Entity

enum class EntityAnchor(val identifier: String) {
    FEET("feet") {
        override fun offset(entity: Entity): Vector3d {
            return Vector3d()
        }
    },

    EYES("eyes") {
        override fun offset(entity: Entity): Vector3d {
            return Vector3d(0.0, entity.eyeHeight.toDouble(), 0.0)
        }
    };

    abstract fun offset(entity: Entity): Vector3d
}
