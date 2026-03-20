package io.github.takenoko4096.starlight.client

import io.github.takenoko4096.starlight.registry.StarlightRegistryAccess
import io.github.takenoko4096.starlight.registry.block.ModBlockConfiguration
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap
import net.minecraft.client.renderer.chunk.ChunkSectionLayer

class StarlightClient : ClientModInitializer {
    override fun onInitializeClient() {
        val blockRegistry = StarlightRegistryAccess.getBlockRegistry()
        for (configuration in blockRegistry.getConfigurations()) {
            val accessor = ModBlockConfiguration.getAccessorForClient(configuration)

            BlockRenderLayerMap.putBlock(
                blockRegistry.getBlock(configuration.resourceKey),
                when (accessor.chunkSectionLayer()) {
                    ModBlockConfiguration.BlockRenderingConfiguration.NonClientChunkSectionLayer.SOLID -> ChunkSectionLayer.SOLID
                    ModBlockConfiguration.BlockRenderingConfiguration.NonClientChunkSectionLayer.CUTOUT -> ChunkSectionLayer.CUTOUT
                    ModBlockConfiguration.BlockRenderingConfiguration.NonClientChunkSectionLayer.TRANSLUCENT -> ChunkSectionLayer.TRANSLUCENT
                    ModBlockConfiguration.BlockRenderingConfiguration.NonClientChunkSectionLayer.TRIPWIRE -> ChunkSectionLayer.TRIPWIRE
                }
            )
        }
    }
}
