package io.github.takenoko4096.starlight.util.text

import net.minecraft.network.chat.Style

class BoldComponentBuilder internal constructor(private val flag: Boolean) : StyleComponentBuilder() {
    override fun update(style: Style): Style {
        return style.withBold(flag)
    }
}
