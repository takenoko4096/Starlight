package io.github.takenoko4096.starlight.util.text

import net.minecraft.network.chat.MutableComponent

abstract class AbstractComponentBuilder protected constructor() {
    internal abstract fun build(): MutableComponent
}
