package io.github.takenoko4096.starlight

import io.github.takenoko4096.starlight.util.item.ItemComponents
import io.github.takenoko4096.starlight.util.item.ItemStackBuilder
import net.minecraft.server.MinecraftServer
import net.minecraft.world.item.Item

class DataDrivenStarlight(val mod: StarlightModInitializer, val server: MinecraftServer) {
    fun itemStackBuilder(item: Item, amount: Int = 1, callback: ItemComponents.() -> Unit = {}): ItemStackBuilder {
        return ItemStackBuilder(this, item, amount, callback)
    }
}
