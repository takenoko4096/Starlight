package io.github.takenoko4096.starlight.registry.block

import io.github.takenoko4096.starlight.StarlightModInitializer
import io.github.takenoko4096.starlight.registry.StarlightRegistry
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.Block

class ModBlockRegistry(mod: StarlightModInitializer) : StarlightRegistry(mod) {
    private val configurations = mutableSetOf<ModBlockConfiguration>()

    private val blocks = mutableMapOf<ResourceKey<Block>, Block>()

    fun register(identifier: String, configuration: ModBlockConfiguration.() -> Unit): Block {
        val o = ModBlockConfiguration(this, identifier)
        o.configuration()
        val block = o.register()
        configurations.add(o)
        return block
    }

    fun getBlock(resourceKey: ResourceKey<Block>): Block {
        return blocks[resourceKey] ?: throw IllegalStateException()
    }

    fun getConfigurations(): Set<ModBlockConfiguration> {
        return configurations.toSet()
    }
}
