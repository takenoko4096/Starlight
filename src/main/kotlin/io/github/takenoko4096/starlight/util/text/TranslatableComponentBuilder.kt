package io.github.takenoko4096.starlight.util.text

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent

class TranslatableComponentBuilder internal constructor(private val key: String, private val insertions: Array<out String>, private val fallback: String?) : AbstractComponentBuilder() {
    override fun build(): MutableComponent {
        return if (insertions.isEmpty() && fallback == null) {
            Component.translatable(key)
        }
        else if (insertions.isEmpty()) {
            Component.translatableWithFallback(key, fallback)
        }
        else if (fallback == null) {
            Component.translatable(key, insertions)
        }
        else {
            Component.translatable(key, fallback, insertions)
        }
    }
}
