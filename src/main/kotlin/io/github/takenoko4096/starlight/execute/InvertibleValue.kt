package io.github.takenoko4096.starlight.execute

import java.util.Objects

class InvertibleValue<T : Any> private constructor(internal val not: Boolean, internal val value: T) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InvertibleValue<*>) return false
        return not == other.not && value == other.value
    }

    override fun hashCode(): Int {
        return Objects.hash(not, value)
    }

    operator fun not(): InvertibleValue<T> {
        return InvertibleValue(not = !not, value)
    }

    companion object {
        fun <T : Any> of(value: T): InvertibleValue<T> {
            return InvertibleValue(not = false, value)
        }
    }
}
