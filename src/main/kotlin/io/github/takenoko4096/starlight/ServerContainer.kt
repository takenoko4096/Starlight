package io.github.takenoko4096.starlight

import io.github.takenoko4096.starlight.item.ItemComponents
import io.github.takenoko4096.starlight.item.ItemStackBuilder
import net.minecraft.server.MinecraftServer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

class ServerContainer internal constructor(val mod: NoctilucaModInitializer, val server: MinecraftServer) {
    fun itemStack(item: Item, amount: Int = 1, callback: ItemComponents.() -> Unit = {}): ItemStack {
        return ItemStackBuilder(item, amount, callback).build(this)
    }
}
