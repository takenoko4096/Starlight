package io.github.takenoko4096.starlight.client

import io.github.takenoko4096.starlight.StarlightModInitializer
import io.github.takenoko4096.starlight.registry.block.BlockRenderingConfiguration
import io.github.takenoko4096.starlight.registry.block.ModBlockConfiguration
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap
import net.minecraft.client.renderer.chunk.ChunkSectionLayer

abstract class StarlightClient(private val mod: StarlightModInitializer) : ClientModInitializer {
    override fun onInitializeClient() {
        val blockRegistry = mod.blockRegistry
        for (configuration in blockRegistry.getConfigurations()) {
            val accessor = ModBlockConfiguration.getAccessorForClient(configuration)

            BlockRenderLayerMap.putBlock(
                blockRegistry.getBlock(configuration.blockResourceKey),
                when (accessor.chunkSectionLayer()) {
                    BlockRenderingConfiguration.NonClientChunkSectionLayer.SOLID -> ChunkSectionLayer.SOLID
                    BlockRenderingConfiguration.NonClientChunkSectionLayer.CUTOUT -> ChunkSectionLayer.CUTOUT
                    BlockRenderingConfiguration.NonClientChunkSectionLayer.TRANSLUCENT -> ChunkSectionLayer.TRANSLUCENT
                    BlockRenderingConfiguration.NonClientChunkSectionLayer.TRIPWIRE -> ChunkSectionLayer.TRIPWIRE
                }
            )
        }

        onInitialize()
    }

    open fun onInitialize() {

    }
}
