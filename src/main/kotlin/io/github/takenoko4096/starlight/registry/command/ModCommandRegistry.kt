package io.github.takenoko4096.starlight.registry.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.github.takenoko4096.starlight.StarlightModInitializer
import io.github.takenoko4096.starlight.registry.StarlightRegistry
import io.github.takenoko4096.starlight.registry.command.node.ConfigurableCommandNode
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

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
}
