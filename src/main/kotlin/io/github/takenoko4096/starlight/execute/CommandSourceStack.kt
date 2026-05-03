package io.github.takenoko4096.starlight.execute

import io.github.takenoko4096.starlight.math.Rotation2f
import io.github.takenoko4096.starlight.math.Vector3d
import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level

class CommandSourceStack(val sender: CommandSender) {
    val entity: Entity? = when (sender) {
        is CommandSender.EntityCommandSender -> sender.entity
        is CommandSender.BlockEntityCommandSender, is CommandSender.LevelCommandSender -> null
    }

    val position: Vector3d = sender.position

    val rotation: Rotation2f = sender.rotation

    val dimension: Level = sender.dimension

    val entityAnchor: EntityAnchor = EntityAnchor.FEET
}
