package io.github.takenoko4096.starlight.registry.block

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import java.util.function.BiConsumer

open class CustomBlock internal constructor(
    properties: BlockBehaviour.Properties,
    private val propertyDefinitions: Set<BlockStatesConfiguration.PropertyDefinition<*>>,
    private val eventDispatcher: BlockEventsConfiguration.BlockEventDispatcher
) : Block(properties) {
    init {
        val defaultState = defaultBlockState()

        for (definition in propertyDefinitions) {
            definition.setDefaultValueTo(defaultState)
        }

        registerDefaultState(defaultState)
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        for (definition in propertyDefinitions) {
            builder.add(definition.property)
        }
    }

    override fun stepOn(level: Level, blockPos: BlockPos, blockState: BlockState, entity: Entity) {
        val event = BlockEventsConfiguration.StepOnEvent(level, blockState, blockPos, entity)
        eventDispatcher.dispatch(BlockEventsConfiguration.StepOnEvent::class, event)
    }

    override fun fallOn(level: Level, blockState: BlockState, blockPos: BlockPos, entity: Entity, d: Double) {
        val event = BlockEventsConfiguration.FallOnEvent(level, blockState, blockPos, entity, d)
        eventDispatcher.dispatch(BlockEventsConfiguration.FallOnEvent::class, event)

        if (event.causeDamage) {
            super.fallOn(level, blockState, blockPos, entity, event.distance)
        }
    }

    override fun onExplosionHit(blockState: BlockState, serverLevel: ServerLevel, blockPos: BlockPos, explosion: Explosion, biConsumer: BiConsumer<ItemStack, BlockPos>) {
        val event = BlockEventsConfiguration.ExplosionHitEvent(serverLevel, blockState, blockPos, explosion, false) {
            biConsumer.accept(it.itemStack, it.blockPos)
        }

        if (!event.ignore) {
            super.onExplosionHit(blockState, serverLevel, blockPos, explosion) { itemStack, blockPos ->
                event.dropHandle(BlockEventsConfiguration.ExplosionHitEvent.ExplosionDrop(itemStack, blockPos))
            }
        }
    }
}