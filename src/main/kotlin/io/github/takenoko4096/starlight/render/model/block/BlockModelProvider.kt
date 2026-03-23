package io.github.takenoko4096.starlight.render.model.block

import io.github.takenoko4096.starlight.render.TexturePath
import io.github.takenoko4096.starlight.render.model.ModelOptions
import io.github.takenoko4096.starlight.render.model.ModelProvider
import io.github.takenoko4096.starlight.render.model.builtin.NonClientBuiltinModel
import io.github.takenoko4096.starlight.render.model.builtin.NonClientBuiltinModelTemplate
import io.github.takenoko4096.starlight.render.model.builtin.NonClientBuiltinTextureSlot
import io.github.takenoko4096.starlight.render.model.custom.NonClientCustomModel
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.Block

class BlockModelProvider internal constructor(resourceKey: ResourceKey<Block>) : ModelProvider<Block>(resourceKey) {
    fun cube(particle: TexturePath, north: TexturePath, south: TexturePath, east: TexturePath, west: TexturePath, up: TexturePath, down: TexturePath, callback: ModelOptions.() -> Unit = {}): NonClientBuiltinModel {
        return NonClientBuiltinModel(
            resourceKey,
            NonClientBuiltinModelTemplate.CUBE,
            mapOf(
                NonClientBuiltinTextureSlot.PARTICLE to particle,
                NonClientBuiltinTextureSlot.NORTH to north,
                NonClientBuiltinTextureSlot.SOUTH to south,
                NonClientBuiltinTextureSlot.EAST to east,
                NonClientBuiltinTextureSlot.WEST to west,
                NonClientBuiltinTextureSlot.UP to up,
                NonClientBuiltinTextureSlot.DOWN to down
            ),
            ModelOptions(callback)
        )
    }

    fun cubeAll(all: TexturePath, callback: ModelOptions.() -> Unit = {}): NonClientBuiltinModel {
        return NonClientBuiltinModel(
            resourceKey,
            NonClientBuiltinModelTemplate.CUBE_ALL,
            mapOf(
                NonClientBuiltinTextureSlot.ALL to all
            ),
            ModelOptions(callback)
        )
    }

    fun custom(modelTemplate: Identifier, textureMapping: Map<String, TexturePath>, callback: ModelOptions.() -> Unit = {}): NonClientCustomModel {
        return NonClientCustomModel(
            resourceKey,
            modelTemplate,
            textureMapping,
            ModelOptions(callback)
        )
    }
}
