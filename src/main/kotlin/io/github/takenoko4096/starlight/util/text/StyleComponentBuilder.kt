package io.github.takenoko4096.starlight.util.text

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style

abstract class StyleComponentBuilder protected constructor() : AbstractComponentBuilder() {
    private var style: Style? = null

    abstract fun update(style: Style): Style

    internal fun receive(style: Style): StyleComponentBuilder {
        this.style = style
        return this
    }

    override fun build(): MutableComponent {
        if (style == null) {
            throw IllegalStateException()
        }

        return Component.empty().withStyle(update(style!!))
    }
}
