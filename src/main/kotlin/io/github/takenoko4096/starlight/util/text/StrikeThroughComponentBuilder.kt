package io.github.takenoko4096.starlight.util.text

import net.minecraft.network.chat.Style

class StrikeThroughComponentBuilder internal constructor(private val flag: Boolean) : StyleComponentBuilder() {
    override fun update(style: Style): Style {
        return style.withStrikethrough(flag)
    }
}
