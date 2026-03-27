package io.github.takenoko4096.starlight.util.item

import net.minecraft.core.component.DataComponentType
import net.minecraft.util.Unit
import net.minecraft.world.item.Item
import java.util.Objects

abstract class ItemComponent<T : Any> protected constructor(protected val type: DataComponentType<T>, protected val value: T) {
    fun set(target: Item.Properties) {
        target.component(type, value)
    }

    override fun hashCode(): Int {
        return Objects.hash(type, value)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ItemComponent<*>) return false
        return type == other.type && value == other.value
    }

    class Valued<T : Any> internal constructor(type: DataComponentType<T>, value: T) : ItemComponent<T>(type, value)

    class NonValued internal constructor(type: DataComponentType<Unit>) : ItemComponent<Unit>(type, Unit.INSTANCE)

    companion object {
        fun <T : Any> valued(type: DataComponentType<T>, value: T): Valued<T> {
            return Valued(type, value)
        }

        fun nonValued(type: DataComponentType<Unit>): NonValued {
            return NonValued(type)
        }
    }
}
