package io.github.takenoko4096.starlight.registry.block

import io.github.takenoko4096.starlight.StarlightDSL
import net.minecraft.world.level.block.state.BlockBehaviour

@StarlightDSL
class CustomBehaviourConfiguration {
    internal var propertyDefinitions: Set<BlockStatesConfiguration.PropertyDefinition<*>>? = null

    private var eventDispatcher: BlockEventsConfiguration.BlockEventDispatcher = BlockEventsConfiguration.BlockEventDispatcher(setOf())

    fun blockStates(callback: BlockStatesConfiguration.() -> Unit): Properties {
        val bsc = BlockStatesConfiguration()
        bsc.callback()
        propertyDefinitions = bsc.build()

        if (propertyDefinitions == null) {
            throw IllegalStateException("propertyDefinitions is null; cannot prepare properties info")
        }

        return Properties(propertyDefinitions!!.toSet())
    }

    fun events(callback: BlockEventsConfiguration.() -> Unit) {
        val bec = BlockEventsConfiguration()
        bec.callback()
        eventDispatcher = bec.build()
    }

    internal fun build(): (BlockBehaviour.Properties) -> CustomBlock {
        if (propertyDefinitions == null) {
            throw IllegalStateException("propertyDefinitions is unset")
        }

        return {
            CustomBlock(it, propertyDefinitions!!, eventDispatcher)
        }
    }
}
