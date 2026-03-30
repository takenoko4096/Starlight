package io.github.takenoko4096.starlight.registry.command.execution

import com.mojang.brigadier.context.CommandContext
import io.github.takenoko4096.starlight.StarlightDSL

@StarlightDSL
class AssignableCommandExecution<S>(context: CommandContext<S>) : AbstractCommandExecution<S>(context) {
    var returns: Int = 1
}
