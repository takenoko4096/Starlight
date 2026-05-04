package io.github.takenoko4096.starlight.registry.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.github.takenoko4096.starlight.StarlightModInitializer
import io.github.takenoko4096.starlight.registry.StarlightRegistry
import io.github.takenoko4096.starlight.registry.command.node.ConfigurableCommandNode
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.util.StringRepresentable
import kotlin.enums.enumEntries

class ModCommandRegistry(mod: StarlightModInitializer) : StarlightRegistry(mod) {
    private val commands = mutableSetOf<(CommandBuildContext, Commands.CommandSelection) -> LiteralArgumentBuilder<CommandSourceStack>>()

    init {
        CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
            commands.forEach {
                val command = it(registryAccess, environment)
                dispatcher.register(command)
            }
        }
    }

    fun register(name: String, callback: ConfigurableCommandNode<CommandSourceStack>.() -> Unit) {
        commands.add { r, e ->
            ConfigurableCommandNode(r, e, name, callback).build()
        }
    }

    fun <T: StringRepresentable> registerArgument(name: String, callback: ModCommandArgumentTypeConfiguration<T>.() -> Unit): () -> ModCommandArgumentType<T> {
        return ModCommandArgumentTypeConfiguration(mod, name, callback).register()
    }

    inline fun <reified E> registerEnumArgument(name: String): () -> ModCommandArgumentType<E> where E : StringRepresentable, E : Enum<E> {
        val values = enumEntries<E>()

        return registerArgument(name) {
            parses {
                val s = reader.readUnquotedString()

                for (value in values) {
                    if (s == value.name.lowercase()) {
                        return@parses value
                    }
                }

                throw exception("'$s' is invalid enum member as $name")
            }

            suggests {
                strings(values.map { it.name.lowercase() })
            }
        }
    }
}
