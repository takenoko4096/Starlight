package io.github.takenoko4096.starlight.registry.block

import io.github.takenoko4096.starlight.StarlightDSL
import io.github.takenoko4096.starlight.registry.translation.TranslationConfiguration
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour
import java.lang.reflect.Field

@StarlightDSL
class ModBlockConfiguration(internal val registry: ModBlockRegistry, internal val identifier: String) {
    val resourceKey: ResourceKey<Block> = ResourceKey.create(
        Registries.BLOCK,
        Identifier.fromNamespaceAndPath(registry.mod.identifier, identifier)
    )

    internal var blockPropertiesConfiguration: BlockPropertiesConfiguration = BlockPropertiesConfiguration(this) {}

    internal var itemProperties: Item.Properties? = null

    internal var customBehaviourCreator: ((BlockBehaviour.Properties) -> Block) = { Block(it) }

    internal var renderingConfig: BlockRenderingConfiguration = BlockRenderingConfiguration(this)

    internal var translation = TranslationConfiguration()

    fun blockProperties(callback: BlockPropertiesConfiguration.() -> Unit) {
        blockPropertiesConfiguration = BlockPropertiesConfiguration(this, callback)
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
        val block = Blocks.register(
            resourceKey,
            customBehaviourCreator,
            BlockBehaviour.Properties.of()
        )

        val properties = blockPropertiesConfiguration.build(block.properties())
        //block.forceCopyFieldFrom("friction", "", "", Block(properties))

        if (itemProperties != null) {
            Items.registerBlock(block, itemProperties!!)
        }

        return block
    }

    private fun BlockBehaviour.forceCopyFieldFrom(mojang: String, intermediary: String, obfuscated: String, from: BlockBehaviour) {
        val clazz = Block::class.java
        val field: Field = try {
            clazz.getDeclaredField(mojang)
        }
        catch (e: NoSuchFieldException) {
            e.printStackTrace()

            try {
                clazz.getDeclaredField(intermediary)
            }
            catch (f: NoSuchFieldException) {
                f.printStackTrace()

                try {
                    clazz.getDeclaredField(obfuscated)
                }
                catch (g: NoSuchFieldException) {
                    g.printStackTrace()

                    throw RuntimeException("Could not find field '$mojang' (Mojang), '$intermediary' (Intermediary), '$obfuscated' (Obfuscated) in class '${clazz.name}'.")
                }
            }
        }

        field.trySetAccessible()
        field.set(this, field.get(from))
    }

    class AccessorForClient internal constructor(private val configuration: ModBlockConfiguration) {
        fun chunkSectionLayer(): BlockRenderingConfiguration.NonClientChunkSectionLayer {
            return configuration.renderingConfig.chunkSectionLayer
        }

        fun blockModelLegacy(): BlockRenderingConfiguration.BlockModel? {
            return configuration.renderingConfig.blockModelConfig.model
        }

        fun blockModelVariants(): BlockRenderingConfiguration.VariantsByProperties? {
            return configuration.renderingConfig.blockModelConfig.variants
        }

        fun blockDefaultItemModel(): BlockRenderingConfiguration.NonClientBlockModel? {
            return configuration.renderingConfig.blockModelConfig.itemModel
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
