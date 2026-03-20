package io.github.takenoko4096.starlight.registry.block

import io.github.takenoko4096.starlight.StarlightDSL
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import kotlin.reflect.KClass

@StarlightDSL
class BlockEventsConfiguration internal constructor() {
    private val handlers = mutableSetOf<BlockEventHandler<*, *>>()

    fun onFallOn(callback: FallOnEvent.() -> Unit) {
        handlers.add(BlockEventHandler(
            FallOnEvent::class,
            callback
        ))
    }

    fun onStepOn(callback: StepOnEvent.() -> Unit) {
        handlers.add(BlockEventHandler(
            StepOnEvent::class,
            callback
        ))
    }

    fun onExplosionHit(callback: ExplosionHitEvent.() -> Unit) {
        handlers.add(BlockEventHandler(
            ExplosionHitEvent::class,
            callback
        ))
    }

    abstract class BlockEvent<R> internal constructor() {}

    class BlockEventHandler<T : BlockEvent<R>, R> internal constructor(
        val clazz: KClass<T>,
        val callback: T.() -> R
    )

    class BlockEventDispatcher internal constructor(private val set: Set<BlockEventHandler<*, *>>) {
        internal fun <T : BlockEvent<R>, R> dispatch(clazz: KClass<T>, event: T) {
            set.filter { it.clazz == clazz }
                .forEach { (it.callback as T.() -> R)(event) }
        }
    }

    class FallOnEvent internal constructor(
        val level: Level,
        val blockState: BlockState,
        val blockPos: BlockPos,
        val entity: Entity,
        var distance: Double,
        var causeDamage: Boolean = true
    ) : BlockEvent<Unit>()

    class StepOnEvent internal constructor(
        val level: Level,
        val blockState: BlockState,
        val blockPos: BlockPos,
        val entity: Entity
    ) : BlockEvent<Unit>()

    class ExplosionHitEvent internal constructor(
        val serverLevel: ServerLevel,
        val blockState: BlockState,
        val blockPos: BlockPos,
        val explosion: Explosion,
        var ignore: Boolean = false,
        var dropHandle: (ExplosionDrop) -> Unit
    ) : BlockEvent<Unit>() {
        class ExplosionDrop internal constructor(
            val itemStack: ItemStack,
            val blockPos: BlockPos
        )
    }

    internal fun build(): BlockEventDispatcher {
        return BlockEventDispatcher(handlers.toSet())
    }
}