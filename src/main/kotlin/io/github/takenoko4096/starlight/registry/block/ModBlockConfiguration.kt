package io.github.takenoko4096.starlight.registry.block

import io.github.takenoko4096.starlight.StarlightDSL
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour

@StarlightDSL
class ModBlockConfiguration(internal val registry: ModBlockRegistry, internal val identifier: String) {
    val resourceKey: ResourceKey<Block> = ResourceKey.create(
        Registries.BLOCK,
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

    fun itemProperties(callback: ItemPropertiesConfiguration.() -> Unit) {
        val ipc = ItemPropertiesConfiguration(this, callback)
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
        if (blockProperties == null) throw IllegalStateException()

        val block = Blocks.register(
            resourceKey,
            customBehaviourCreator,
            blockProperties!!
        )

        if (itemProperties != null) {
            Items.registerBlock(block, itemProperties!!)
        }

        return block
    }

    class AccessorForClient internal constructor(private val configuration: ModBlockConfiguration) {
        fun chunkSectionLayer(): BlockRenderingConfiguration.NonClientChunkSectionLayer {
            return configuration.renderingConfig.chunkSectionLayer
        }

        fun blockModel(): BlockRenderingConfiguration.BlockModel {
            return configuration.renderingConfig.blockModelConfig.model
        }

        fun translation(): TranslationConfiguration {
            return configuration.translation
        }
    }

    companion object {
        fun getAccessorForClient(configuration: ModBlockConfiguration): AccessorForClient {
            return AccessorForClient(configuration)
        }
    }
}
