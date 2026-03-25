package io.github.takenoko4096.starlight.registry.block

import io.github.takenoko4096.starlight.StarlightDSL
import io.github.takenoko4096.starlight.registry.translation.TranslationConfiguration
import io.github.takenoko4096.starlight.render.NonClientChunkSectionLayer
import io.github.takenoko4096.starlight.render.model.NonClientModel
import io.github.takenoko4096.starlight.render.model.block.NonClientBlockModelVariant
import io.github.takenoko4096.starlight.render.model.block.PropertyVariants
import io.github.takenoko4096.starlight.render.model.block.PropertyVariants0
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState

@StarlightDSL
class ModBlockConfiguration(internal val registry: ModBlockRegistry, internal val identifier: String) {
    val blockResourceKey: ResourceKey<Block> = ResourceKey.create(
        Registries.BLOCK,
        Identifier.fromNamespaceAndPath(registry.mod.identifier, identifier)
    )

    val itemResourceKey: ResourceKey<Item> = ResourceKey.create(
        Registries.ITEM,
        Identifier.fromNamespaceAndPath(registry.mod.identifier, identifier)
    )

    internal var blockProperties: BlockBehaviour.Properties? = null

    internal var itemProperties: Item.Properties? = null

    internal var customBehaviourCreator: ((BlockBehaviour.Properties) -> Block) = { Block(it) }

    internal var renderingConfig: BlockRenderingConfiguration = BlockRenderingConfiguration(this)

    internal var translation = TranslationConfiguration()

    fun blockProperties(callback: BlockPropertiesConfiguration.() -> Unit) {
        val bpc = BlockPropertiesConfiguration(this, callback)
        blockProperties = bpc.build()
    }

    fun itemProperties(callback: BlockItemPropertiesConfiguration.() -> Unit) {
        val ipc = BlockItemPropertiesConfiguration(this, callback)
        itemProperties = ipc.build()
    }

    fun rendering(callback: BlockRenderingConfiguration.() -> Unit) {
        val brc = BlockRenderingConfiguration(this)
        brc.callback()
        renderingConfig = brc
    }

    fun customBehaviour(callback: CustomBehaviourConfiguration.() -> Unit): CustomBehaviourInfo {
        val cbc = CustomBehaviourConfiguration()
        cbc.callback()
        customBehaviourCreator = cbc.build()
        return CustomBehaviourInfo(Properties(cbc.propertyDefinitions.toSet()))
    }

    fun translation(callback: TranslationConfiguration.() -> Unit) {
        val tc = TranslationConfiguration()
        tc.callback()
        translation = tc
    }

    internal fun register(): Block {
        if (blockProperties == null) {
            throw IllegalStateException("'blockProperties' is unset!")
        }

        val block = Blocks.register(
            blockResourceKey,
            customBehaviourCreator,
            blockProperties!!
        )

        if (itemProperties != null) {
            Items.registerBlock(block, itemProperties!!)
        }

        return block
    }

    class AccessorForClient internal constructor(private val configuration: ModBlockConfiguration) {
        fun chunkSectionLayer(): NonClientChunkSectionLayer {
            return configuration.renderingConfig.layerConfig.layer
        }

        fun blockModelLegacy(): BlockRenderingConfiguration.SingleArgBlockModel? {
            return configuration.renderingConfig.modelConfig.blockModelConfig.model
        }

        fun blockModelVariants(): PropertyVariants? {
            return configuration.renderingConfig.modelConfig.blockModelConfig.variants
        }

        fun blockDefaultItemModel(): NonClientModel? {
            return configuration.renderingConfig.modelConfig.itemModelConfig.model
        }

        fun translation(): TranslationConfiguration {
            return configuration.translation
        }

        fun getTint(blockPos: BlockPos, blockState: BlockState, blockAndTintGetter: BlockAndTintGetter, i: Int): Int? {
            return BlockRenderingConfiguration.TintConfiguration(
                blockPos,
                blockState,
                blockAndTintGetter,
                i,
                configuration.renderingConfig.tintGetter
            ).color
        }
    }

    companion object {
        fun getAccessorForClient(configuration: ModBlockConfiguration): AccessorForClient {
            return AccessorForClient(configuration)
        }
    }
}
