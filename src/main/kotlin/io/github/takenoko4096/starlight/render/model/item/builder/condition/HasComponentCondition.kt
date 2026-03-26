package io.github.takenoko4096.starlight.render.model.item.builder.condition

import net.minecraft.core.component.DataComponentType

class HasComponentCondition<C : Any>(val type: DataComponentType<C>, val ignoreDefault: Boolean) : Condition<C>()
