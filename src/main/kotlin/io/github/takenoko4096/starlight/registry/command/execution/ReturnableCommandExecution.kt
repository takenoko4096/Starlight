package io.github.takenoko4096.starlight.registry.command.execution

import com.mojang.brigadier.context.CommandContext
import io.github.takenoko4096.starlight.StarlightDSL

@StarlightDSL
class ReturnableCommandExecution<S>(context: CommandContext<S>) : AbstractCommandExecution<S>(context) {

}
