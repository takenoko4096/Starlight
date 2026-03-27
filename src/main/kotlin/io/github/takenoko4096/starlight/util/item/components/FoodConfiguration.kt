package io.github.takenoko4096.starlight.util.item.components

import io.github.takenoko4096.starlight.StarlightDSL
import io.github.takenoko4096.starlight.StarlightModInitializer
import net.minecraft.core.component.DataComponents
import net.minecraft.world.food.FoodProperties

@StarlightDSL
class FoodConfiguration(mod: StarlightModInitializer, callback: FoodConfiguration.() -> Unit) : AbstractComponentConfiguration<FoodProperties>(mod, DataComponents.FOOD) {
    var nutrition: Int = 0

    var saturation: Float = 0f

    var canAlwaysEat: Boolean = false

    init {
        callback()
    }

    override fun build(): FoodProperties {
        return FoodProperties(
            nutrition,
            saturation,
            canAlwaysEat
        )
    }
}
