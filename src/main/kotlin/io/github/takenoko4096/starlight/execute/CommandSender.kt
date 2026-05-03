package io.github.takenoko4096.starlight.execute

import io.github.takenoko4096.starlight.math.Position3i
import io.github.takenoko4096.starlight.math.Rotation2f
import io.github.takenoko4096.starlight.math.Vector3d
import net.minecraft.commands.CommandSourceStack
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.server.MinecraftServer
import net.minecraft.server.permissions.LevelBasedPermissionSet
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.CommandBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.CommandBlockEntity
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3
import java.util.Objects

sealed class CommandSender(val registryAccess: HolderLookup.Provider) {
    abstract val server: MinecraftServer

    abstract val position: Vector3d

    abstract val rotation: Rotation2f

    abstract val dimension: Level

    abstract override fun hashCode(): Int

    abstract override operator fun equals(other: Any?): Boolean

    class EntityCommandSender(val entity: Entity) : CommandSender(entity.registryAccess()) {
        override val server: MinecraftServer
            get() = entity.level().server ?: throw IllegalStateException()

        override val position: Vector3d
            get() = Vector3d.from(entity.position())

        override val rotation: Rotation2f
            get() = Rotation2f.from(entity.rotationVector)

        override val dimension: Level
            get() = entity.level()

        override fun hashCode(): Int {
            return entity.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            return if (other is EntityCommandSender) entity == other.entity else false
        }
    }

    class BlockEntityCommandSender(val blockEntity: BlockEntity) : CommandSender(blockEntity.level?.registryAccess() ?: throw IllegalStateException()) {
        override val server: MinecraftServer
            get() = blockEntity.level?.server ?: throw IllegalStateException()

        override val position: Vector3d
            get() = Position3i.from(blockEntity.blockPos).bottomCenter()

        override val rotation: Rotation2f
            get() {
                if (blockEntity is CommandBlockEntity) {
                    val facing = blockEntity.blockState.getValue(CommandBlock.FACING)
                    val vector = facing.step()
                    return Vector3d(vector.x.toDouble(), vector.y.toDouble(), vector.z.toDouble()).toRotation2f()
                }
                else {
                    return Rotation2f()
                }
            }

        override val dimension: Level
            get() = blockEntity.level ?: throw IllegalStateException()

        override fun hashCode(): Int {
            return blockEntity.blockPos.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            return if (other is BlockEntityCommandSender) blockEntity.blockPos == other.blockEntity.blockPos else false
        }
    }

    class LevelCommandSender(override val dimension: Level) : CommandSender(dimension.registryAccess()) {
        override val server: MinecraftServer
            get() = dimension.server ?: throw IllegalStateException()

        override val position: Vector3d
            get() = Position3i.from(dimension.respawnData.globalPos.pos).bottomCenter()

        override val rotation: Rotation2f
            get() = Rotation2f()

        override fun hashCode(): Int {
            return dimension.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            return if (other is LevelCommandSender) dimension == other.dimension else false
        }
    }

    companion object {
        fun overworld(registryAccess: HolderLookup.Provider): LevelCommandSender {
            return LevelCommandSender(
                registryAccess
                    .lookupOrThrow(Registries.DIMENSION)
                    .getOrThrow(Level.OVERWORLD)
                    .value()
            )
        }
    }
}
