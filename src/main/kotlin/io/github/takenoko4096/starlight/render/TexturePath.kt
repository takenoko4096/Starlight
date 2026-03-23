package io.github.takenoko4096.starlight.render

import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.Block

open class TexturePath private constructor(val identifier: Identifier) {
    fun suffixed(suffix: String): TexturePath {
        return TexturePath(identifier.withSuffix("_$suffix"))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TexturePath) return false

        if (identifier != other.identifier) return false

        return true
    }

    override fun hashCode(): Int {
        return identifier.hashCode()
    }

    companion object {
        internal fun blockDefault(resourceKey: ResourceKey<Block>): TexturePath {
            return TexturePath(resourceKey.identifier().withPrefix("block/"))
        }
    }
}
