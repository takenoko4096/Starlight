package io.github.takenoko4096.starlight.item.components

import io.github.takenoko4096.starlight.StarlightDSL
import io.github.takenoko4096.starlight.StarlightModInitializer
import net.minecraft.core.HolderSet
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.enchantment.Repairable

@StarlightDSL
class RepairableConfiguration(mod: StarlightModInitializer, callback: RepairableConfiguration.() -> Unit) : AbstractComponentConfiguration<Repairable>(mod, DataComponents.REPAIRABLE) {
    private var items: HolderSet<Item>? = null

    init {
        callback()
    }

    fun items(tag: TagKey<Item>) {
        items = BuiltInRegistries.ITEM.getOrThrow(tag)
    }

    override fun build(): Repairable {
        if (items == null) {
            throw IllegalStateException("'items' is unset")
        }

        return Repairable(
            items!!
        )
    }
}
