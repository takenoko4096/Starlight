package io.github.takenoko4096.starlight.util.item

import io.github.takenoko4096.starlight.DataDrivenStarlight
import io.github.takenoko4096.starlight.StarlightModInitializer
import io.github.takenoko4096.starlight.util.item.components.EnchantmentsConfiguration
import net.minecraft.server.MinecraftServer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemStackTemplate

class ItemStackBuilder internal constructor(data: DataDrivenStarlight, private val item: Item, private val amount: Int = 1, callback: ItemComponents.() -> Unit = {}) : ItemComponents(data.mod, data.server.registryAccess(), callback) {
    fun build(): ItemStack {
        val template = ItemStackTemplate(item, amount)
        apply(template)
        return template.create()
    }
}
