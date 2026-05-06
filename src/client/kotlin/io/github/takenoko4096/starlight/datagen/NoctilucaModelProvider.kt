package io.github.takenoko4096.starlight.datagen

import io.github.takenoko4096.starlight.NoctilucaModInitializer
import io.github.takenoko4096.starlight.datagen.model.BlockModelVariantsRegistrar
import io.github.takenoko4096.starlight.datagen.model.builder.ClientItemModelHandle
import io.github.takenoko4096.starlight.registry.block.ModBlockConfiguration
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.model.TexturedModel
import io.github.takenoko4096.starlight.registry.block.BlockRenderingConfiguration.SingleArgBlockModel.SingleArgBlockTextureMap
import io.github.takenoko4096.starlight.registry.item.ModItemConfiguration
import io.github.takenoko4096.starlight.registry.item.ModItemRegistry
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput

class NoctilucaModelProvider(private val mod: NoctilucaModInitializer, output: FabricPackOutput) : FabricModelProvider(output) {
    override fun generateBlockStateModels(blockModelGenerators: BlockModelGenerators) {
        val blockRegistry = mod.blockRegistry

        for (configuration in blockRegistry.getConfigurations()) {
            val block = blockRegistry.getBlock(configuration.blockResourceKey)
            val accessor = ModBlockConfiguration.getAccessorForClient(configuration)
            configuration.withItem()

            val model = accessor.blockModelLegacy()
            when (model?.textureMap) {
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
                SingleArgBlockTextureMap.ANVIL -> {
                    blockModelGenerators.createAnvil(block)
                }
                SingleArgBlockTextureMap.DOOR -> {
                    blockModelGenerators.createDoor(block)
                }
                SingleArgBlockTextureMap.LANTERN -> {
                    blockModelGenerators.createLantern(block)
                }
                null -> {}
            }

            val variants = accessor.blockModelVariants()

            val registrar = BlockModelVariantsRegistrar(
                blockModelGenerators,
                block,
                accessor.blockItemModel(),
                variants,
                accessor.family()
            )
            registrar.register()
        }
    }

    override fun generateItemModels(itemModelGenerators: ItemModelGenerators) {
        val itemRegistry: ModItemRegistry = mod.itemRegistry

        for (configuration in itemRegistry.getConfigurations()) {
            val item = itemRegistry.getItem(configuration.itemResourceKey)
            val accessor = ModItemConfiguration.getAccessor(configuration)

            ClientItemModelHandle.registerModel(itemModelGenerators, item, accessor.getModelHandle())
        }
    }

    override fun getName(): String = "StarlightModelProvider"
}
