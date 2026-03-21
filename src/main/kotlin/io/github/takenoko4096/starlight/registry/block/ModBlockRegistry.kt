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
        blocks[o.resourceKey] = block
        return block
    }

    fun getBlock(resourceKey: ResourceKey<Block>): Block {
        return blocks[resourceKey] ?: throw IllegalArgumentException("ブロック '${resourceKey.identifier()}' が Modブロックレジストリに見つかりませんでした: ${blocks.keys.map { it.identifier() }}")
    }

    fun getConfigurations(): Set<ModBlockConfiguration> {
        return configurations.toSet()
    }
}
