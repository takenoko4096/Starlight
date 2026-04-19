package io.github.takenoko4096.starlight.registry.command.execution

import com.mojang.brigadier.context.CommandContext
import io.github.takenoko4096.starlight.text.SectionComponentBuilder
import io.github.takenoko4096.starlight.text.VanillaColor
import io.github.takenoko4096.starlight.text.component
import net.minecraft.commands.CommandSourceStack

class ErrorHandlerExecution<S>(context: CommandContext<S>, val error: Throwable) : AbstractCommandExecution<S>(context) {
    fun CommandContext<CommandSourceStack>.sendErrorMessage(callback: SectionComponentBuilder.() -> Unit) {
        source.sendSystemMessage(component {
            textColor(VanillaColor.RED)
            section(callback)
        })
    }
}
