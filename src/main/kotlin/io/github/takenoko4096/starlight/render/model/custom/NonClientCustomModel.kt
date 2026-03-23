package io.github.takenoko4096.starlight.render.model.custom

import io.github.takenoko4096.starlight.render.TexturePath
import io.github.takenoko4096.starlight.render.model.ModelOptions
import io.github.takenoko4096.starlight.render.model.NonClientModel
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey

class NonClientCustomModel(
    resourceKey: ResourceKey<*>,
    val parent: Identifier,
    val mapping: Map<String, TexturePath>,
    options: ModelOptions
) : NonClientModel(resourceKey, options)
