package io.github.takenoko4096.starlight.registry.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder

class Command<S> internal constructor(
    internal val literalArgumentBuilder: LiteralArgumentBuilder<S>,
    val name: String
)
