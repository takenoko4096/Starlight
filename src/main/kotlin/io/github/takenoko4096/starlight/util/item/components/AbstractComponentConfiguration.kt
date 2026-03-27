package io.github.takenoko4096.starlight.util.item.components

import io.github.takenoko4096.starlight.StarlightModInitializer
import io.github.takenoko4096.starlight.util.item.ItemComponent
import net.minecraft.core.component.DataComponentType

abstract class AbstractComponentConfiguration<T : Any>(protected val mod: StarlightModInitializer, protected val type: DataComponentType<T>) {
    internal fun toComponent(): ItemComponent.Valued<T> {
        return ItemComponent.valued(type, build())
    }

    protected abstract fun build(): T
}
