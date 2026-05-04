package io.github.takenoko4096.starlight.registry.command

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.github.takenoko4096.starlight.StarlightDSL
import io.github.takenoko4096.starlight.StarlightModInitializer
import io.github.takenoko4096.starlight.registry.command.node.SuggestibleCommandNode
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry
import net.minecraft.commands.synchronization.SingletonArgumentInfo
import net.minecraft.util.StringRepresentable
import java.util.concurrent.CompletableFuture

@StarlightDSL
class ModCommandArgumentTypeConfiguration<T : StringRepresentable>(private val mod: StarlightModInitializer, private val name: String, callback: ModCommandArgumentTypeConfiguration<T>.() -> Unit) {
    private var parser: (StringReader.() -> T)? = null

    private var suggester: ((CommandContext<*>, SuggestionsBuilder) -> CompletableFuture<Suggestions>)? = null

    init {
        callback()
    }

    fun parses(callback: ArgumentParser.() -> T) {
        parser = {
            val p = ArgumentParser(this)
            p.callback()
        }
    }

    fun suggests(callback: SuggestibleCommandNode.UserInputDependingSuggestionProvider<*>.() -> Unit) {
        suggester = { context, suggestionsBuilder ->
            val s = SuggestibleCommandNode.UserInputDependingSuggestionProvider(context, suggestionsBuilder)
            s.callback()
            s.suggestAbove()
            suggestionsBuilder.buildFuture()
        }
    }

    fun register(): () -> ModCommandArgumentType<T> {
        if (argumentTypes.contains(name)) {
            throw IllegalArgumentException("argument type ${mod.identifier}:$name is already registered!")
        }

        if (parser == null) {
            throw IllegalStateException("'parses' is not specified")
        }

        if (suggester == null) {
            throw IllegalStateException("'suggests' is not specified")
        }

        val identifier = mod.identifierOf(name)
        val constructor = { ModCommandArgumentType(identifier, parser!!, suggester!!) }

        ArgumentTypeRegistry.registerArgumentType(
            identifier,
            ModCommandArgumentType::class.java,
            SingletonArgumentInfo.contextFree(constructor)
        )

        argumentTypes.add(name)

        return constructor
    }

    companion object {
        private val argumentTypes = mutableSetOf<String>()
    }

    class ArgumentParser internal constructor(val reader: StringReader) {
        fun exception(message: String): CommandSyntaxException {
            return CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().createWithContext(reader, message)
        }
    }
}
