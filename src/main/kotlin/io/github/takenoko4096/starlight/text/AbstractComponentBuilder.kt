package io.github.takenoko4096.starlight.text

import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style

abstract class AbstractComponentBuilder protected constructor(protected var style: Style) {
    internal abstract fun build(): MutableComponent
}
