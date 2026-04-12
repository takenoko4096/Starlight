package io.github.takenoko4096.starlight

import io.github.takenoko4096.starlight.util.item.ItemComponents
import io.github.takenoko4096.starlight.util.item.ItemStackBuilder
import net.minecraft.server.MinecraftServer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

class DataDrivenStarlight internal constructor(val mod: StarlightModInitializer, val server: MinecraftServer) {
    fun itemStack(item: Item, amount: Int = 1, callback: ItemComponents.() -> Unit = {}): ItemStack {
        return ItemStackBuilder(item, amount, callback).build(this)
    }
}
