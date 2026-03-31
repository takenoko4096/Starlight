package io.github.takenoko4096.starlight.util.text

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor

class SectionComponentBuilder private constructor(callback: SectionComponentBuilder.() -> Unit) : AbstractComponentBuilder() {
    private val children = mutableListOf<AbstractComponentBuilder>()

    init {
        callback()
    }

    fun text(text: String) {
        children.add(TextComponentBuilder(text))
    }

    fun bold(flag: Boolean) {
        children.add(BoldComponentBuilder(flag))
    }

    fun italic(flag: Boolean) {
        children.add(ItalicComponentBuilder(flag))
    }

    fun underlined(flag: Boolean) {
        children.add(UnderlinedComponentBuilder(flag))
    }

    fun obfuscated(flag: Boolean) {
        children.add(ObfuscatedComponentBuilder(flag))
    }

    fun strikeThrough(flag: Boolean) {
        children.add(StrikeThroughComponentBuilder(flag))
    }

    fun color(color: Int) {
        children.add(ColorComponentBuilder(color))
    }

    fun section(callback: SectionComponentBuilder.() -> Unit) {
        children.add(SectionComponentBuilder(callback))
    }

    override fun build(): MutableComponent {
        val root = Component.empty()
        var component = root

        for (builder in children) {
            if (builder is StyleComponentBuilder) {
                component = builder.receive(component.style).build()
            }
            else {
                component.append(builder.build())
            }
        }

        return root
    }

    companion object {
        fun main() {
            SectionComponentBuilder {
                text("a")
                color(2)

                section {
                    bold(true)
                }
            }
        }
    }
}
