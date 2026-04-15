package io.github.takenoko4096.starlight.item.components

import io.github.takenoko4096.starlight.StarlightModInitializer
import io.github.takenoko4096.starlight.item.ItemComponent
import net.minecraft.core.component.DataComponentType

abstract class AbstractComponentConfiguration<T : Any>(protected val mod: StarlightModInitializer, protected val type: DataComponentType<T>) {
    internal open fun toComponent(): ItemComponent<T> {
        return ItemComponent.valued(type, build())
    }

    protected abstract fun build(): T
}
