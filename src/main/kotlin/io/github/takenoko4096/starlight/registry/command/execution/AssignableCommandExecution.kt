package io.github.takenoko4096.starlight.registry.command.execution

import com.mojang.brigadier.context.CommandContext
import io.github.takenoko4096.starlight.StarlightDSL
import io.github.takenoko4096.starlight.text.SectionComponentBuilder
import io.github.takenoko4096.starlight.text.VanillaColor
import io.github.takenoko4096.starlight.text.component
import net.minecraft.commands.CommandSourceStack

@StarlightDSL
class AssignableCommandExecution<S>(context: CommandContext<S>) : AbstractCommandExecution<S>(context) {
    var returns: Int = 1

    fun CommandContext<CommandSourceStack>.successful(returns: Int = 1, callback: SectionComponentBuilder.() -> Unit): Int {
        source.sendSystemMessage(component(callback))
        this@AssignableCommandExecution.returns = returns
        return returns
    }

    fun CommandContext<CommandSourceStack>.failure(callback: SectionComponentBuilder.() -> Unit): Int {
        source.sendSystemMessage(component {
            textColor(VanillaColor.RED)
            section(callback)
        })
        returns = 0
        return 0
    }
}
