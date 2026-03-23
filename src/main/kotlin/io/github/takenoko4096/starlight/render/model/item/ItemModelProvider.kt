package io.github.takenoko4096.starlight.render.model.item

import io.github.takenoko4096.starlight.render.model.ModelProvider
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item

class ItemModelProvider internal constructor(resourceKey: ResourceKey<Item>) : ModelProvider<Item>(resourceKey) {

}
