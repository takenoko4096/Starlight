package io.github.takenoko4096.starlight.client

import io.github.takenoko4096.starlight.StarlightModInitializer
import io.github.takenoko4096.starlight.registry.block.ModBlockConfiguration
import io.github.takenoko4096.starlight.render.NonClientChunkSectionLayer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.minecraft.client.renderer.chunk.ChunkSectionLayer

abstract class StarlightClientModInitializer(private val mod: StarlightModInitializer) : ClientModInitializer {
    override fun onInitializeClient() {
        val blockRegistry = mod.blockRegistry
        for (configuration in blockRegistry.getConfigurations()) {
            val accessor = ModBlockConfiguration.getAccessorForClient(configuration)
            val block = blockRegistry.getBlock(configuration.blockResourceKey)

            // chunk section layer
            BlockRenderLayerMap.putBlock(
                block,
                when (accessor.chunkSectionLayer()) {
                    NonClientChunkSectionLayer.SOLID -> ChunkSectionLayer.SOLID
                    NonClientChunkSectionLayer.CUTOUT -> ChunkSectionLayer.CUTOUT
                    NonClientChunkSectionLayer.TRANSLUCENT -> ChunkSectionLayer.TRANSLUCENT
                    NonClientChunkSectionLayer.TRIPWIRE -> ChunkSectionLayer.TRIPWIRE
                }
            )

            // tint
            ColorProviderRegistry.BLOCK.register({ blockState, blockAndTintGetter, blockPos, i ->
                if (blockAndTintGetter != null && blockPos != null) {
                    return@register accessor.getTint(blockPos, blockState, blockAndTintGetter, i)
                        ?: -1
                }

                return@register -1
            },block)
        }

        onInitialize()
    }

    open fun onInitialize() {

    }
}
