package io.github.takenoko4096.starlight.registry

import com.mojang.brigadier.CommandDispatcher
import io.github.takenoko4096.starlight.StarlightModInitializer
import io.github.takenoko4096.starlight.registry.command.Command
import io.github.takenoko4096.starlight.registry.command.node.ConfigurableCommandNode
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

class ModCommandRegistry(mod: StarlightModInitializer) : StarlightRegistry(mod) {
    private val commands = mutableSetOf<(CommandBuildContext, Commands.CommandSelection) -> Command<CommandSourceStack>>()

    fun register(name: String, callback: ConfigurableCommandNode<CommandSourceStack>.() -> Unit) {
        commands.add { r, e ->
            ConfigurableCommandNode(r, e, name, callback).build()
        }
    }

    internal fun use(dispatcher: CommandDispatcher<CommandSourceStack>, registryAccess: CommandBuildContext, environment: Commands.CommandSelection) {
        commands.forEach {
            dispatcher.register(
                it(registryAccess, environment).literalArgumentBuilder
            )
        }
    }
}
