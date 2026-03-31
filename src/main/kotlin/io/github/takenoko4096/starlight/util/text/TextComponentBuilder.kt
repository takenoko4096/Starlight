package io.github.takenoko4096.starlight.util.text

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent

class TextComponentBuilder internal constructor(private val text: String) : AbstractComponentBuilder() {
    override fun build(): MutableComponent {
        return Component.literal(text)
    }
}
