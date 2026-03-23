package io.github.takenoko4096.starlight.render.model.block

import net.minecraft.world.level.block.state.properties.Property

class PropertyVariants1<T : Comparable<T>> internal constructor(val property: Property<T>) : PropertyVariants() {
    val selects: MutableSet<NonClientBlockSelect1<T>> = mutableSetOf()

    fun NonClientBlockModelVariant.useWhen(value1: T) {
        selects.add(
            NonClientBlockSelect1(value1, this)
        )
    }
}
