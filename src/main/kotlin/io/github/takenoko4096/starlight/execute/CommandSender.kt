package io.github.takenoko4096.starlight.execute

import io.github.takenoko4096.starlight.math.Vector3d
import net.minecraft.core.HolderLookup
import net.minecraft.world.entity.Entity

sealed class CommandSender(val registryAccess: HolderLookup.Provider) {
    abstract fun getPosition(): Vector3d

    class EntityCommandSender(registryAccess: HolderLookup.Provider, val entity: Entity) : CommandSender(registryAccess) {
        override fun getPosition(): Vector3d {
            return Vector3d.from(entity.position())
        }
    }
}
