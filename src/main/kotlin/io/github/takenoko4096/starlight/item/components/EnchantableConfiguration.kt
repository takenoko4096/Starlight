package io.github.takenoko4096.starlight.item.components

import io.github.takenoko4096.starlight.StarlightDSL
import io.github.takenoko4096.starlight.StarlightModInitializer
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.enchantment.Enchantable

@StarlightDSL
class EnchantableConfiguration(mod: StarlightModInitializer, callback: EnchantableConfiguration.() -> Unit) : AbstractComponentConfiguration<Enchantable>(mod, DataComponents.ENCHANTABLE) {
    var enchantmentAptitude: Int? = null

    init {
        callback()
    }

    override fun build(): Enchantable {
        if (enchantmentAptitude == null) {
            throw IllegalStateException("'enchantment aptitude' is unset")
        }

        return Enchantable(enchantmentAptitude!!)
    }
}
