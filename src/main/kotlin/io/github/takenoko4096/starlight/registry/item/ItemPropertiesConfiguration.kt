package io.github.takenoko4096.starlight.registry.item

import io.github.takenoko4096.starlight.StarlightDSL
import io.github.takenoko4096.starlight.util.item.ItemComponentsBuilder
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.Item

@StarlightDSL
class ItemPropertiesConfiguration internal constructor(private val configuration: ModItemConfiguration, callback: ItemPropertiesConfiguration.() -> Unit) {
    private var translationKey: String? = null

    private var components = ItemComponentsBuilder(configuration.registry.mod, null) {}

    init {
        callback()
    }

    fun translationKeyOf(value: String) {
        translationKey = "item.${configuration.registry.mod.identifier}.$value"
    }

    fun translationKeyAuto() {
        translationKeyOf(configuration.identifier)
    }

    fun components(callback: ItemComponentsBuilder.() -> Unit) {
        components = ItemComponentsBuilder(configuration.registry.mod, null, callback)
    }

    internal fun build(): Item.Properties {
        val properties = Item.Properties()

        if (translationKey == null) throw IllegalStateException()
        properties.overrideDescription(translationKey!!)

        components.build(properties)

        return properties
    }
}
