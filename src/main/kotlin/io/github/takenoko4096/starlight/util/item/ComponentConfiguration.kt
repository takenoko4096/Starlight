package io.github.takenoko4096.starlight.util.item

import io.github.takenoko4096.starlight.StarlightModInitializer
import net.minecraft.core.component.DataComponentType

abstract class ComponentConfiguration<T : Any>(protected val mod: StarlightModInitializer, protected val type: DataComponentType<T>) {
    fun toComponent(): AbstractItemComponent.Valued<T> {
        return AbstractItemComponent.valued(type, build())
    }

    protected abstract fun build(): T
}
