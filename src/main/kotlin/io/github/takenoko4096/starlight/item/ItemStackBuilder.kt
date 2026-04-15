package io.github.takenoko4096.starlight.item

import io.github.takenoko4096.starlight.DataDrivenStarlight
import io.github.takenoko4096.starlight.StarlightModInitializer
import io.github.takenoko4096.starlight.util.item.components.EnchantmentsConfiguration
import net.minecraft.core.HolderLookup
import net.minecraft.server.MinecraftServer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemStackTemplate

class ItemStackBuilder internal constructor(private val item: Item, private val amount: Int = 1, private val callback: ItemComponents.() -> Unit = {}) {
    fun build(mod: StarlightModInitializer, dataSource: HolderLookup.Provider?): ItemStack {
        val template = ItemStackTemplate(item, amount)

        val components = ItemComponents(mod, dataSource, callback)
        components.apply(template)

        return template.create()
    }

    fun build(data: DataDrivenStarlight): ItemStack {
        return build(data.mod, data.server.registryAccess())
    }

    fun build(mod: StarlightModInitializer): ItemStack {
        return build(mod, null)
    }
}
