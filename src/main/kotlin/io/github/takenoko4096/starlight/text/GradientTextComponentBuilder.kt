package io.github.takenoko4096.starlight.text

import io.github.takenoko4096.starlight.StarlightDSL
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FontDescription
import net.minecraft.network.chat.Style

@StarlightDSL
class GradientTextComponentBuilder(private val left: RgbColor, private val right: RgbColor, style: Style, callback: GradientTextComponentBuilder.() -> Unit) : AbstractComponentBuilder(style) {
    private var chars: String = ""

    private val decorations = mutableListOf<Pair<Int, Style>>()

    init {
        callback()
    }

    fun text(text: String) {
        chars += text
    }

    fun text(text: Char) = text(text.toString())

    fun space() = text(' ')

    fun linebreak() = text('\n')

    fun bold(flag: Boolean = true) {
        decorations.add(chars.length to (decorations.lastOrNull()?.component2() ?: Style.EMPTY).withBold(flag))
    }

    fun italic(flag: Boolean = true) {
        decorations.add(chars.length to (decorations.lastOrNull()?.component2() ?: Style.EMPTY).withItalic(flag))
    }

    fun underlined(flag: Boolean = true) {
        decorations.add(chars.length to (decorations.lastOrNull()?.component2() ?: Style.EMPTY).withUnderlined(flag))
    }

    fun obfuscated(flag: Boolean = true) {
        decorations.add(chars.length to (decorations.lastOrNull()?.component2() ?: Style.EMPTY).withObfuscated(flag))
    }

    fun strikeThrough(flag: Boolean = true) {
        decorations.add(chars.length to (decorations.lastOrNull()?.component2() ?: Style.EMPTY).withStrikethrough(flag))
    }

    fun font(font: FontDescription? = null) {
        decorations.add(chars.length to (decorations.lastOrNull()?.component2() ?: Style.EMPTY).withFont(font))
    }

    private fun color(t: Double): RgbColor {
        val l = left.toHsv()
        val r = right.toHsv()

        var deltaH = r.h - l.h
        if (deltaH > 180) deltaH -= 360
        if (deltaH < -180) deltaH += 360

        val h = (l.h + deltaH * t + 360) % 360
        val s = l.s + (r.s - l.s) * t
        val v = l.v + (r.v - r.v) * t

        return HsvColor(h, s, v).toRgb()
    }

    override fun build(): Component {
        if (chars.length <= 1) {
            throw IllegalStateException()
        }

        val component = Component.empty().withStyle(style)

        val ignoredCount = chars.count { char -> char == '\n' || char == ' ' }
        val length = (chars.length - 1 - ignoredCount)

        for ((index, char) in chars.withIndex()) {
            val t = index.toDouble() / length
            val color = color(t)

            val style0 = decorations.find { index >= it.component1() }?.component2()
            val literal = Component.literal(char.toString())
            val component0 = if (char == '\n' || char == ' ') {
                literal
            }
            else if (style0 == null) {
                literal.withStyle(color.applyToText(style))
            }
            else {
                literal.withStyle(color.applyToText(style)).withStyle(style0)
            }
            component.append(component0)
        }

        return component
    }
}
