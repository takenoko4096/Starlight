package io.github.takenoko4096.starlight.registry.command.execution

import com.mojang.brigadier.context.CommandContext
import io.github.takenoko4096.starlight.StarlightDSL
import kotlin.reflect.KClass

@StarlightDSL
abstract class AbstractCommandExecution<S>(val context: CommandContext<S>) {
    operator fun <T : Any> String.get(clazz: KClass<T>): T {
        return context.getArgument(this, clazz.java)
    }
}
