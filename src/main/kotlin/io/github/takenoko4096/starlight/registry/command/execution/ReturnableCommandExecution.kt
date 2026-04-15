package io.github.takenoko4096.starlight.registry.command.execution

import com.mojang.brigadier.context.CommandContext
import io.github.takenoko4096.starlight.StarlightDSL
import io.github.takenoko4096.starlight.text.SectionComponentBuilder
import io.github.takenoko4096.starlight.text.VanillaColor
import io.github.takenoko4096.starlight.text.component
import net.minecraft.commands.CommandSourceStack

@StarlightDSL
class ReturnableCommandExecution<S>(context: CommandContext<S>) : AbstractCommandExecution<S>(context) {
    fun CommandContext<CommandSourceStack>.successful(returns: Int = 1, callback: SectionComponentBuilder.() -> Unit): Int {
        source.sendSystemMessage(component(callback))
        return returns
    }

    fun CommandContext<CommandSourceStack>.failure(callback: SectionComponentBuilder.() -> Unit): Int {
        source.sendSystemMessage(component {
            textColor(VanillaColor.RED)
            section(callback)
        })
        return 0
    }
}
