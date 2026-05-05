package io.github.takenoko4096.starlight.registry.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.github.takenoko4096.starlight.StarlightModInitializer
import io.github.takenoko4096.starlight.registry.StarlightRegistry
import io.github.takenoko4096.starlight.registry.command.node.ConfigurableCommandNode
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.synchronization.SingletonArgumentInfo
import net.minecraft.util.StringRepresentable

class ModCommandRegistry(public override val mod: StarlightModInitializer) : StarlightRegistry(mod) {
    private val commands = mutableSetOf<(CommandBuildContext, Commands.CommandSelection) -> LiteralArgumentBuilder<CommandSourceStack>>()

    private val argumentTypes = mutableSetOf<String>()

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

    fun <T: StringRepresentable, U : ModCommandArgumentType<T>> registerArgumentTypeByConfiguredSingleton(type: U) {
        if (type.identifier.path in argumentTypes) {
            throw IllegalArgumentException("argument type ${type.identifier} is already registered!")
        }

        ArgumentTypeRegistry.registerArgumentType(
            type.identifier,
            type::class.java,
            SingletonArgumentInfo.contextFree { type }
        )

        argumentTypes.add(type.identifier.path)
    }

    inline fun <reified T : StringRepresentable> registerArgumentType(name: String, noinline configuration: ModCommandArgumentTypeConfiguration<T>.() -> Unit): ModCommandArgumentType<T> {
        val type = object : ModCommandArgumentType<T>(mod, name, configuration) {}
        registerArgumentTypeByConfiguredSingleton(type)
        return type
    }

    inline fun <reified T> registerEnumArgumentType(name: String): ModCommandEnumArgumentType<T> where T : StringRepresentable, T : Enum<T> {
        val type = object : ModCommandEnumArgumentType<T>(mod, name, T::class) {}
        registerArgumentTypeByConfiguredSingleton(type)
        return type
    }
}
