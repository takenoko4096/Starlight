package io.github.takenoko4096.starlight.registry.command

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.resources.Identifier
import net.minecraft.util.StringRepresentable
import java.util.concurrent.CompletableFuture

class ModCommandArgumentType<T : StringRepresentable>(
    private val identifier: Identifier,
    private val parser: StringReader.() -> T,
    private val suggester: (CommandContext<*>, SuggestionsBuilder) -> CompletableFuture<Suggestions>
) : ArgumentType<T> {
    override fun parse(reader: StringReader): T {
        return reader.parser()
    }

    override fun <S> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        return suggester(context, builder)
    }
}
