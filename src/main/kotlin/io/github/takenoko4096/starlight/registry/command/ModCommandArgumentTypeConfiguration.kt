package io.github.takenoko4096.starlight.registry.command

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
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
import java.util.Objects
import java.util.concurrent.CompletableFuture

@StarlightDSL
class ModCommandArgumentTypeConfiguration<T : StringRepresentable>(private val mod: StarlightModInitializer, private val name: String, callback: ModCommandArgumentTypeConfiguration<T>.() -> Unit) {
    internal var parser: (ArgumentParser.() -> T)? = null

    internal var suggester: (SuggestibleCommandNode.UserInputDependingSuggestionProvider<*>.() -> Unit)? = null

    init {
        callback()
    }

    fun parses(callback: ArgumentParser.() -> T) {
        if (parser != null) {
            throw IllegalStateException("HA??????????????????????? parser ${mod.identifier} $name $callback")
        }

        parser = callback
    }

    fun suggests(callback: SuggestibleCommandNode.UserInputDependingSuggestionProvider<*>.() -> Unit) {
        if (suggester != null) {
            throw IllegalStateException("HA??????????????????????? suggester ${mod.identifier} $name $callback")
        }

        suggester = callback
    }

    class ArgumentParser internal constructor(val reader: StringReader) {
        fun exception(message: String): CommandSyntaxException {
            return CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().createWithContext(reader, message)
        }
    }
}
