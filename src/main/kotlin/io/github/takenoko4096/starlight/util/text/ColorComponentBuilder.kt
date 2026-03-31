package io.github.takenoko4096.starlight.util.text

import net.minecraft.network.chat.Style

class ColorComponentBuilder internal constructor(private val color: Int): StyleComponentBuilder() {
    override fun update(style: Style): Style {
        return style.withColor(color)
    }
}
