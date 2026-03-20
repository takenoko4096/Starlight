package io.github.takenoko4096.starlight.registry.block

import io.github.takenoko4096.starlight.StarlightDSL
import net.minecraft.world.level.block.state.BlockBehaviour

@StarlightDSL
class CustomBehaviourConfiguration {
    internal var propertyDefinitions: Set<BlockStatesConfiguration.PropertyDefinition<*>> = setOf()

    private var eventDispatcher: BlockEventsConfiguration.BlockEventDispatcher = BlockEventsConfiguration.BlockEventDispatcher(setOf())

    fun blockStates(callback: BlockStatesConfiguration.() -> Unit): Properties {
        val bsc = BlockStatesConfiguration()
        bsc.callback()
        propertyDefinitions = bsc.build()
        return Properties(propertyDefinitions.toSet())
    }

    fun events(callback: BlockEventsConfiguration.() -> Unit) {
        val bec = BlockEventsConfiguration()
        bec.callback()
        eventDispatcher = bec.build()
    }

    internal fun build(): (BlockBehaviour.Properties) -> CustomBlock {
        return {
            CustomBlock(it, propertyDefinitions, eventDispatcher)
        }
    }
}