package io.github.takenoko4096.starlight

import io.github.takenoko4096.starlight.item.ItemComponents
import io.github.takenoko4096.starlight.item.ItemStackBuilder
import net.minecraft.server.MinecraftServer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

class DataDrivenStarlight internal constructor(val mod: StarlightModInitializer, val server: MinecraftServer) {
    fun itemStack(item: Item, amount: Int = 1, callback: io.github.takenoko4096.starlight.item.ItemComponents.() -> Unit = {}): ItemStack {
        return _root_ide_package_.io.github.takenoko4096.starlight.item.ItemStackBuilder(item, amount, callback).build(this)
    }
}
