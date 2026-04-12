package io.github.takenoko4096.starlight.registry.item

import io.github.takenoko4096.starlight.StarlightDSL
import io.github.takenoko4096.starlight.registry.translation.ModTranslationConfiguration
import io.github.takenoko4096.starlight.render.TexturePath
import io.github.takenoko4096.starlight.render.model.item.ItemModelProvider
import io.github.takenoko4096.starlight.render.model.item.builder.ItemModelBuilder
import io.github.takenoko4096.starlight.render.model.item.builder.ItemModelHandle
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item

class ModItemConfiguration(internal val registry: ModItemRegistry, internal val identifier: String) {
    val itemResourceKey: ResourceKey<Item> = ResourceKey.create(
        Registries.ITEM,
        Identifier.fromNamespaceAndPath(registry.mod.identifier, identifier)
    )

    internal var customBehaviourCreator: ((Item.Properties) -> Item) = { Item(it) }

    internal var itemProperties: Item.Properties? = null

    internal var renderingConfig: ItemRenderingConfiguration = ItemRenderingConfiguration(this)

    internal var translation = ModTranslationConfiguration()

    fun itemProperties(callback: ItemPropertiesConfiguration.() -> Unit) {
        val ipc = ItemPropertiesConfiguration(this, callback)
        itemProperties = ipc.build()
    }

    fun rendering(callback: ItemRenderingConfiguration.() -> Unit) {
        val brc = ItemRenderingConfiguration(this)
        brc.callback()
        renderingConfig = brc
    }

    fun translation(callback: ModTranslationConfiguration.() -> Unit) {
        val tc = ModTranslationConfiguration()
        tc.callback()
        translation = tc
    }

    internal fun register(): Item {
        if (itemProperties == null) {
            throw IllegalStateException("'itemProperties' is unset!")
        }

        val item = customBehaviourCreator(itemProperties!!)
        Registry.register(BuiltInRegistries.ITEM, itemResourceKey, item)

        return item
    }

    @StarlightDSL
    class ItemRenderingConfiguration(private val configuration: ModItemConfiguration) {
        internal var modelConfig: ItemModelConfiguration = ItemModelConfiguration(configuration) {}

        fun model(callback: ItemModelConfiguration.() -> Unit) {
            modelConfig = ItemModelConfiguration(configuration, callback)
            if (modelConfig.handle == null) {
                throw IllegalStateException("please use 'use()' to specify model handler")
            }
        }
    }

    @StarlightDSL
    class ItemModelConfiguration(private val configuration: ModItemConfiguration, callback: ItemModelConfiguration.() -> Unit) {
        internal var handle: ItemModelHandle?

        val itemDefaultTexturePath = TexturePath.itemDefault(configuration.itemResourceKey)

        val itemModels = ItemModelProvider(configuration.itemResourceKey)

        init {
            handle = null
            callback()
        }

        fun handling(callback: ItemModelBuilder.() -> Unit) {
            handle = ItemModelBuilder(callback).build()
        }
    }

    class AccessorForClient internal constructor(private val configuration: ModItemConfiguration) {
        fun getModelHandle(): ItemModelHandle {
            return configuration.renderingConfig.modelConfig.handle ?: throw IllegalStateException()
        }

        fun translation(): ModTranslationConfiguration {
            return configuration.translation
        }
    }

    companion object {
        fun getAccessor(configuration: ModItemConfiguration): AccessorForClient {
            return AccessorForClient(configuration)
        }
    }
}
