package io.github.takenoko4096.starlight.util.text

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style

class TextComponentBuilder internal constructor(private val text: String, private val style: Style) : AbstractComponentBuilder() {
    override fun build(): MutableComponent {
        return Component.literal(text).withStyle(style)
    }
}
