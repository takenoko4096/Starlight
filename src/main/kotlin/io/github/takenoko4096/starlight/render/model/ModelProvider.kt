package io.github.takenoko4096.starlight.render.model

import net.minecraft.resources.ResourceKey

abstract class ModelProvider<T : Any>(protected val resourceKey: ResourceKey<T>)
