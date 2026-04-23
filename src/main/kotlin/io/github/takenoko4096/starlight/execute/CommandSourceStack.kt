package io.github.takenoko4096.starlight.execute

import io.github.takenoko4096.starlight.math.Rotation2f
import io.github.takenoko4096.starlight.math.Vector3d
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level

class CommandSourceStack(sender: CommandSender) {
    private var executor: Entity?

    private var position: Vector3d

    private var rotation: Rotation2f

    private var dimension: Level

    private var entityAnchor: EntityAnchor

    init {
        executor = null
        position = Vector3d()
        rotation = Rotation2f()
        dimension = sender.registryAccess
            .lookupOrThrow(Registries.DIMENSION)
            .getOrThrow(Level.OVERWORLD)
            .value()
        entityAnchor = EntityAnchor.FEET
    }

    fun clone() {

    }
}
