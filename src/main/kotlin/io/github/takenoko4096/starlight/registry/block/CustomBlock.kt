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
import org.jetbrains.annotations.ApiStatus
import java.lang.reflect.Field
import java.util.function.BiConsumer

@ApiStatus.Experimental
open class CustomBlock internal constructor(
    properties: BlockBehaviour.Properties,
    private val propertyDefinitions: Set<BlockStatesConfiguration.PropertyDefinition<*>>,
    private val eventDispatcher: BlockEventsConfiguration.BlockEventDispatcher
) : Block(properties) {
    /**
     * 呼び出し順は
     * Block(properties)
     *      の中の createBlockStateDefinition()
     *          の中の this.propertyDefinitions
     * ↓
     * private val propertyDefinitions (プロパティ初期化)
     *
     * propertyDefinitions の初期化や init {} の前に呼ばれるのが createBlockStateDefinition()
     */

    init {
        val builder = StateDefinition.Builder<Block, BlockState>(this)
        for (definition in propertyDefinitions) {
            builder.add(definition.property)
        }

        setField(
            "properties",
            "field_23155",
            "O",
            this,
            properties
        )

        val stateDefinition = builder.create(
            { it.defaultBlockState() },
            { a, b, c -> BlockState(a, b, c) }
        )

        setField(
            "stateDefinition",
            "field_10647",
            "C",
            this,
            stateDefinition
        )

        registerDefaultState(stateDefinition.any())

        var defaultState = defaultBlockState()

        for (definition in propertyDefinitions) {
            defaultState = definition.setDefaultValueTo(defaultState)
        }

        registerDefaultState(defaultState)
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

    companion object {
        fun setField(mojang: String, intermediary: String, obfuscated: String, block: Block, value: Any) {
            val clazz = Block::class.java
            val field: Field = try {
                clazz.getDeclaredField(mojang)
            }
            catch (e: NoSuchFieldException) {
                e.printStackTrace()

                try {
                    clazz.getDeclaredField(intermediary)
                }
                catch (f: NoSuchFieldException) {
                    f.printStackTrace()

                    try {
                        clazz.getDeclaredField(obfuscated)
                    }
                    catch (g: NoSuchFieldException) {
                        g.printStackTrace()

                        throw RuntimeException("Could not find field 'stateDefinition' (Mojang), 'field_10647' (Intermediary), 'C' (Obfuscated) in class '${clazz.name}'.")
                    }
                }
            }
            field.trySetAccessible()
            field.set(block, value)
        }
    }
}
