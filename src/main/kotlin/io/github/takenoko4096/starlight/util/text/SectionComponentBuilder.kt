package io.github.takenoko4096.starlight.util.text

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor

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

    fun bold(flag: Boolean = true) {
        style = style.withBold(flag)
    }

    fun italic(flag: Boolean = true) {
        style = style.withItalic(flag)
    }

    fun underlined(flag: Boolean = true) {
        style = style.withUnderlined(flag)
    }

    fun obfuscated(flag: Boolean = true) {
        style = style.withObfuscated(flag)
    }

    fun strikeThrough(flag: Boolean = true) {
        style = style.withStrikethrough(flag)
    }

    fun textColor(color: Int? = null) {
        style = if (color == null) style.withColor(null as TextColor?) else style.withColor(color)
    }

    fun shadowColor(color: Int? = null) {
        style = if (color == null) style.withoutShadow() else style.withShadowColor(color)
    }

    fun textColor(color: VanillaColor) {
        textColor(color.color)
    }

    fun shadowColor(color: VanillaColor) {
        shadowColor(color.color)
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
