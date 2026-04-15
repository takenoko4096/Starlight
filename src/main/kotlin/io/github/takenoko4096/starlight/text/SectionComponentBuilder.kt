package io.github.takenoko4096.starlight.text

import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FontDescription
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import net.minecraft.resources.Identifier
import net.minecraft.world.item.component.ResolvableProfile

class SectionComponentBuilder internal constructor(parent: SectionComponentBuilder?, callback: SectionComponentBuilder.() -> Unit) : AbstractComponentBuilder(parent?.copyCurrentStyle() ?: Style.EMPTY) {
    private val children = mutableListOf<AbstractComponentBuilder>()

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

    fun text(text: Char) {
        children.add(TextComponentBuilder(text.toString(), copyCurrentStyle()))
    }

    fun translate(key: String, vararg insertions: String, fallback: String? = null) {
        children.add(TranslatableComponentBuilder(key, insertions, fallback, copyCurrentStyle()))
    }

    fun atlas(atlas: Identifier, sprite: Identifier, fallback: (SectionComponentBuilder.() -> Unit)? = null) {
        children.add(AtlasComponentBuilder(atlas, sprite, SectionComponentBuilder(this, fallback ?: {}), copyCurrentStyle()))
    }

    fun player(profile: ResolvableProfile, showHeadOverlay: Boolean = false, fallback: (SectionComponentBuilder.() -> Unit)? = null) {
        children.add(PlayerComponentBuilder(profile, showHeadOverlay, SectionComponentBuilder(this, fallback ?: {}), copyCurrentStyle()))
    }

    fun keybind(name: String) {
        children.add(KeybindComponentBuilder(name, copyCurrentStyle()))
    }

    fun component(component: Component) {
        children.add(object : AbstractComponentBuilder(copyCurrentStyle()) {
            override fun build(): MutableComponent {
                return component.copy()
            }
        })
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

    fun font(font: FontDescription? = null) {
        style = style.withFont(font)
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

    fun onClick(clickEvent: ClickEvent) {
        style = style.withClickEvent(clickEvent)
    }

    fun onHover(hoverEvent: HoverEvent) {
        style = style.withHoverEvent(hoverEvent)
    }

    override fun build(): MutableComponent {
        val component = Component.empty()

        for (builder in children) {
            component.append(builder.build())
        }

        return component
    }
}
