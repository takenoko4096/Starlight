package io.github.takenoko4096.starlight.util.item.components

import io.github.takenoko4096.starlight.StarlightDSL
import io.github.takenoko4096.starlight.StarlightModInitializer
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.Rarity

@StarlightDSL
class RarityConfiguration(mod: StarlightModInitializer, callback: RarityConfiguration.() -> Unit) : AbstractComponentConfiguration<Rarity>(mod, DataComponents.RARITY) {
    private var rarity: Rarity = Rarity.COMMON

    init {
        callback()
    }

    fun common() {
        rarity = Rarity.COMMON
    }

    fun uncommon() {
        rarity = Rarity.UNCOMMON
    }

    fun rare() {
        rarity = Rarity.RARE
    }

    fun epic() {
        rarity = Rarity.EPIC
    }

    override fun build(): Rarity {
        return rarity
    }
}