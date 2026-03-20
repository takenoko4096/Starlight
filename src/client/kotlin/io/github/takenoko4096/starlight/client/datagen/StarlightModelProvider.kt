package io.github.takenoko4096.starlight.client.datagen

import io.github.takenoko4096.starlight.StarlightModInitializer
import io.github.takenoko4096.starlight.registry.block.BlockRenderingConfiguration
import io.github.takenoko4096.starlight.registry.block.ModBlockConfiguration
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.model.TexturedModel
import io.github.takenoko4096.starlight.registry.block.BlockRenderingConfiguration.SingleArgBlockModel.SingleArgBlockTextureMap

class StarlightModelProvider(private val mod: StarlightModInitializer, output: FabricDataOutput) : FabricModelProvider(output) {
    override fun generateBlockStateModels(blockModelGenerators: BlockModelGenerators) {
        // ここでエラーが出るかどうか、すべてはrunDatagenの内部実装にかかっている
        // 多分内部でminecraft mainを起動してるだけだからいけるとは思うんだけどね
        val blockRegistry = mod.blockRegistry

        for (configuration in blockRegistry.getConfigurations()) {
            val block = blockRegistry.getBlock(configuration.resourceKey)
            val accessor = ModBlockConfiguration.getAccessorForClient(configuration)

            when (val model = accessor.blockModel()) {
                is BlockRenderingConfiguration.SingleArgBlockModel -> {
                    when (model.textureMap) {
                        SingleArgBlockTextureMap.TRIVIAL_CUBE -> {
                            blockModelGenerators.createTrivialCube(block)
                        }
                        SingleArgBlockTextureMap.TRIVIAL_COLUMN -> {
                            blockModelGenerators.createTrivialBlock(block, TexturedModel.COLUMN)
                        }
                        SingleArgBlockTextureMap.TRIVIAL_COLUMN_ALT -> {
                            blockModelGenerators.createTrivialBlock(block, TexturedModel.COLUMN_ALT)
                        }
                        SingleArgBlockTextureMap.TRIVIAL_COLUMN_HORIZONTAL -> {
                            blockModelGenerators.createTrivialBlock(block, TexturedModel.COLUMN_HORIZONTAL)
                        }
                        SingleArgBlockTextureMap.TRIVIAL_COLUMN_HORIZONTAL_ALT -> {
                            blockModelGenerators.createTrivialBlock(block, TexturedModel.COLUMN_HORIZONTAL_ALT)
                        }
                        SingleArgBlockTextureMap.GENRIC_CUBE -> {
                            blockModelGenerators.createGenericCube(block)
                        }
                        SingleArgBlockTextureMap.ANVIL -> {
                            blockModelGenerators.createAnvil(block)
                        }
                        SingleArgBlockTextureMap.DOOR -> {
                            blockModelGenerators.createDoor(block)
                        }
                        SingleArgBlockTextureMap.LANTERN -> {
                            blockModelGenerators.createLantern(block)
                        }
                    }
                }
                is BlockRenderingConfiguration.FlexibleBlockModel -> {

                }
            }
        }
    }

    override fun generateItemModels(itemModelGenerators: ItemModelGenerators) {

    }

    override fun getName(): String = "StarlightModelProvider"
}
