package io.github.takenoko4096.starlight.util.item

import io.github.takenoko4096.starlight.StarlightDSL
import io.github.takenoko4096.starlight.StarlightModInitializer
import net.minecraft.core.Holder
import net.minecraft.core.RegistryAccess
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.server.MinecraftServer
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.ItemEnchantments

@StarlightDSL
class EnchantmentsConfiguration internal constructor(mod: StarlightModInitializer, private val dataSource: RegistryAccess, callback: EnchantmentsConfiguration.() -> Unit) : ComponentConfiguration<ItemEnchantments>(mod, DataComponents.ENCHANTMENTS) {
    private var enchantments = mutableMapOf<Holder<Enchantment>, Int>()

    init {
        callback()
    }

    private fun enchant(enchantment: ResourceKey<Enchantment>, level: Int) {
        val registry = dataSource.lookupOrThrow(Registries.ENCHANTMENT)
        val holder = registry.getOrThrow(enchantment)
        enchantments[holder] = level
    }

    override fun build(): ItemEnchantments {
        val mutable = ItemEnchantments.Mutable(ItemEnchantments.EMPTY)
        enchantments.forEach {
            mutable.set(it.key, it.value)
        }
        return mutable.toImmutable()
    }
}
