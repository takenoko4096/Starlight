package io.github.takenoko4096.starlight.util.item.components

import io.github.takenoko4096.starlight.StarlightModInitializer
import net.minecraft.core.RegistryAccess
import net.minecraft.core.component.DataComponentType

abstract class AbstractDataDrivenComponentConfiguration<T : Any>(mod: StarlightModInitializer, protected val dataSource: RegistryAccess, type: DataComponentType<T>) : AbstractComponentConfiguration<T>(mod, type) {
    abstract override fun build(): T
}
