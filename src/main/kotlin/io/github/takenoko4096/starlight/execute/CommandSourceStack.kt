package io.github.takenoko4096.starlight.execute

import io.github.takenoko4096.starlight.math.Rotation2f
import io.github.takenoko4096.starlight.math.Vector3d
import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level

class CommandSourceStack(sender: CommandSender) {
    private var executor: Entity? = null

    private var position: Vector3d = Vector3d()

    private var rotation: Rotation2f = Rotation2f()

    private var dimension: Level = sender.registryAccess
        .lookupOrThrow(Registries.DIMENSION)
        .getOrThrow(Level.OVERWORLD)
        .value()

    private var entityAnchor: EntityAnchor = EntityAnchor.FEET

    init {

    }

    fun clone(): CommandSourceStack {
        return CommandSourceStack(

        )
    }
}
