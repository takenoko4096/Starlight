package io.github.takenoko4096.starlight.render.model

import net.minecraft.resources.ResourceKey

abstract class NonClientModel(private val resourceKey: ResourceKey<*>, val options: ModelOptions) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NonClientModel) return false

        if (resourceKey != other.resourceKey) return false
        if (options.suffix != other.options.suffix) return false

        return true
    }

    override fun hashCode(): Int {
        var result = resourceKey.hashCode()
        result = 31 * result + options.suffix.hashCode()
        return result
    }
}
