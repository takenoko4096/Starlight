package io.github.takenoko4096.starlight.util.item

import net.minecraft.core.component.DataComponentType
import net.minecraft.util.Unit
import net.minecraft.world.item.Item

abstract class AbstractItemComponent<T : Any> protected constructor(protected val type: DataComponentType<T>, protected val value: T) {
    fun set(target: Item.Properties) {
        target.component(type, value)
    }

    class Valued<T : Any> internal constructor(type: DataComponentType<T>, value: T) : AbstractItemComponent<T>(type, value)

    class NonValued internal constructor(type: DataComponentType<Unit>) : AbstractItemComponent<Unit>(type, net.minecraft.util.Unit.INSTANCE)

    companion object {
        fun <T : Any> valued(type: DataComponentType<T>, value: T): Valued<T> {
            return Valued(type, value)
        }

        fun nonValued(type: DataComponentType<Unit>): NonValued {
            return NonValued(type)
        }
    }
}
