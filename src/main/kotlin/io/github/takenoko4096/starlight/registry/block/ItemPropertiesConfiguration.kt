package io.github.takenoko4096.starlight.registry.block

import io.github.takenoko4096.starlight.StarlightDSL
import net.minecraft.world.item.Item

@StarlightDSL
class ItemPropertiesConfiguration internal constructor(private val configuration: ModBlockConfiguration, callback: ItemPropertiesConfiguration.() -> Unit) {
    init {
        callback()
    }

    private var translationKey: String? = null

    fun translationKeyOf(value: String) {
        translationKey = "block.${configuration.registry.mod.identifier}.$value"
    }

    fun translationKeyAuto() {
        translationKeyOf(configuration.identifier)
    }

    internal fun build(): Item.Properties {
        val properties = Item.Properties()
        if (translationKey == null) throw IllegalStateException()
        else properties.overrideDescription(translationKey!!)
        return properties
    }
}