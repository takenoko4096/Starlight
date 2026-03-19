package io.github.takenoko4096.starlight.client.datagen

import io.github.takenoko4096.starlight.registry.StarlightRegistryAccess
import io.github.takenoko4096.starlight.registry.block.ModBlockConfiguration
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.model.ModelTemplates
import net.minecraft.client.data.models.model.TexturedModel

class StarlightModelProvider(output: FabricDataOutput) : FabricModelProvider(output) {
    override fun generateBlockStateModels(blockModelGenerators: BlockModelGenerators) {
        val blockRegistry = StarlightRegistryAccess.getBlockRegistry()

        for (configuration in blockRegistry.getConfigurations()) {
            val block = blockRegistry.getBlock(configuration.resourceKey)
            val model = blockRegistry.getBlockModelForClient(configuration)

            when (model) {
                is ModBlockConfiguration.BlockRenderingConfiguration.TrivialBlockModel -> {
                    when (model.textureMap) {
                        ModBlockConfiguration.BlockRenderingConfiguration.TrivialBlockModel.TrivialBlockTextureMap.CUBE -> {
                            blockModelGenerators.createTrivialCube(block)
                        }
                    }
                }
            }
        }
    }

    override fun generateItemModels(itemModelGenerators: ItemModelGenerators) {

    }

    override fun getName(): String = "StarlightModelProvider"
}
