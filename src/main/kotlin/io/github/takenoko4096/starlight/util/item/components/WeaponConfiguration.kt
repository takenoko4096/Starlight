package io.github.takenoko4096.starlight.util.item.components

import io.github.takenoko4096.starlight.StarlightDSL
import io.github.takenoko4096.starlight.StarlightModInitializer
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.component.Weapon

@StarlightDSL
class WeaponConfiguration(mod: StarlightModInitializer, callback: WeaponConfiguration.() -> Unit) : AbstractComponentConfiguration<Weapon>(mod, DataComponents.WEAPON) {
    var disableBlockingPerSeconds: Float = 0f

    var itemDamagePerAttack: Int = 1

    init {
        callback()
    }

    override fun build(): Weapon {
        return Weapon(
            itemDamagePerAttack,
            disableBlockingPerSeconds
        )
    }
}