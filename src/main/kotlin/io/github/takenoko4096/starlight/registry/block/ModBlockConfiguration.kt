package io.github.takenoko4096.starlight.registry.block

import io.github.takenoko4096.starlight.StarlightDSL
import io.github.takenoko4096.starlight.registry.item.ModItemConfiguration
import io.github.takenoko4096.starlight.registry.translation.ModTranslationConfiguration
import io.github.takenoko4096.starlight.render.model.NonClientModel
import io.github.takenoko4096.starlight.render.model.block.PropertyVariants
import io.github.takenoko4096.starlight.render.model.item.builder.ItemModelHandle
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
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

    internal var translation = ModTranslationConfiguration()

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
        if (renderingConfig.modelConfig == null) {
            throw IllegalStateException("ブロック '${blockResourceKey.identifier()}' のモデル設定がありません")
        }
    }

    fun customBehaviour(callback: CustomBehaviourConfiguration.() -> Unit): CustomBehaviourInfo {
        val cbc = CustomBehaviourConfiguration()
        cbc.callback()
        customBehaviourCreator = cbc.build()
        return CustomBehaviourInfo(Properties(cbc.propertyDefinitions.toSet()))
    }

    fun translation(callback: ModTranslationConfiguration.() -> Unit) {
        val tc = ModTranslationConfiguration()
        tc.callback()
        translation = tc
    }

    fun withItem() {
        itemProperties {}
    }

    internal fun register(): Block {
        if (blockProperties == null) {
            throw IllegalStateException("'blockProperties' is unset!")
        }

        val block = customBehaviourCreator(blockProperties!!)
        Registry.register(BuiltInRegistries.BLOCK, blockResourceKey, block)

        if (itemProperties != null) {
            val item = BlockItem(block, itemProperties!!)
            Registry.register(BuiltInRegistries.ITEM, itemResourceKey, item)
        }

        return block
    }

    class AccessorForClient internal constructor(private val configuration: ModBlockConfiguration) {
        fun blockModelLegacy(): BlockRenderingConfiguration.SingleArgBlockModel? {
            return configuration.renderingConfig.modelConfig?.blockModelConfig?.model
        }

        fun blockModelVariants(): PropertyVariants? {
            return configuration.renderingConfig.modelConfig?.blockModelConfig?.variants
        }

        fun blockItemModel(): ItemModelHandle? {
            return configuration.renderingConfig.modelConfig?.itemModelConfig?.handle
        }

        fun translation(): ModTranslationConfiguration {
            return configuration.translation
        }

        fun defaultTint(): (BlockState) -> Int {
            return configuration.renderingConfig.tintConfig.defaultColorGetter
        }

        fun inWorldTint(): (BlockState, BlockPos, Level) -> Int {
            return configuration.renderingConfig.tintConfig.colorGetter
        }

        fun terrainParticleTint(): (BlockState, BlockPos, Level) -> Int {
            return configuration.renderingConfig.tintConfig.particleColorGetter
        }
    }

    companion object {
        fun getAccessorForClient(configuration: ModBlockConfiguration): AccessorForClient {
            return AccessorForClient(configuration)
        }
    }
}
