package io.github.takenoko4096.starlight.text

import net.minecraft.network.chat.Style

abstract class ObjectComponentBuilder internal constructor(protected val fallback: SectionComponentBuilder, style: Style) : AbstractComponentBuilder(style)
