package io.github.takenoko4096.starlight.item.components

import io.github.takenoko4096.starlight.StarlightModInitializer
import io.github.takenoko4096.starlight.item.AbstractItemComponent
import io.github.takenoko4096.starlight.item.ItemComponent
import net.minecraft.core.HolderLookup
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentType
import net.minecraft.resources.ResourceKey

abstract class AbstractDataDrivenComponentConfiguration<T : Any>(mod: StarlightModInitializer, protected val dataSource: HolderLookup.Provider, type: DataComponentType<T>) : AbstractComponentConfiguration<T>(mod, type) {
    override fun toComponent(): ItemComponent<T> {
        return ItemComponent.valued(type, build())
    }

    abstract override fun build(): T
}
