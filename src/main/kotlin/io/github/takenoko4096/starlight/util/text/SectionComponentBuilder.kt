package io.github.takenoko4096.starlight.util.text

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import net.minecraft.network.chat.contents.PlainTextContents

class SectionComponentBuilder internal constructor(parent: SectionComponentBuilder?, callback: SectionComponentBuilder.() -> Unit) : AbstractComponentBuilder() {
    private val children = mutableListOf<AbstractComponentBuilder>()

    private var style: Style = parent?.copyCurrentStyle() ?: Style.EMPTY

    init {
        callback()
    }

    private fun copyCurrentStyle(): Style {
        val copy = Style.EMPTY
            .withColor(style.color)
            .withBold(style.isBold)
            .withItalic(style.isItalic)
            .withUnderlined(style.isUnderlined)
            .withObfuscated(style.isObfuscated)
            .withStrikethrough(style.isStrikethrough)
            .withInsertion(style.insertion)
            .withFont(style.font)
            .withClickEvent(style.clickEvent)
            .withHoverEvent(style.hoverEvent)

        return if (style.shadowColor == null) copy.withoutShadow()
        else copy.withShadowColor(style.shadowColor!!)
    }

    fun text(text: String) {
        children.add(TextComponentBuilder(text, copyCurrentStyle()))
    }

    fun bold(flag: Boolean) {
        style = style.withBold(flag)
    }

    fun italic(flag: Boolean) {
        style = style.withItalic(flag)
    }

    fun underlined(flag: Boolean) {
        style = style.withUnderlined(flag)
    }

    fun obfuscated(flag: Boolean) {
        style = style.withObfuscated(flag)
    }

    fun strikeThrough(flag: Boolean) {
        style = style.withStrikethrough(flag)
    }

    fun color(color: Int) {
        style = style.withColor(color)
    }

    fun shadow(color: Int?) {
        style = if (color == null) style.withoutShadow() else style.withShadowColor(color)
    }

    fun section(callback: SectionComponentBuilder.() -> Unit) {
        children.add(SectionComponentBuilder(this, callback))
    }

    override fun build(): MutableComponent {
        val component = Component.empty()

        for (builder in children) {
            component.append(builder.build())
        }

        return component
    }
}
