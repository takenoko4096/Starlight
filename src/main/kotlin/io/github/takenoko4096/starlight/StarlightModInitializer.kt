package io.github.takenoko4096.starlight

import io.github.takenoko4096.starlight.registry.block.ModBlockRegistry
import io.github.takenoko4096.starlight.registry.command.ModCommandRegistry
import io.github.takenoko4096.starlight.registry.command.execution.AssignableCommandExecution
import io.github.takenoko4096.starlight.registry.creativetab.ModCreativeModeTabRegistry
import io.github.takenoko4096.starlight.registry.item.ModItemRegistry
import io.github.takenoko4096.starlight.registry.tag.ModTagRegistry
import io.github.takenoko4096.starlight.registry.translation.ModTranslationRegistry
import io.github.takenoko4096.starlight.render.TexturePath
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.commands.CommandSourceStack
import net.minecraft.resources.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

abstract class StarlightModInitializer(val identifier: String) : ModInitializer {
    val logger: Logger = LoggerFactory.getLogger(identifier)

    val itemRegistry: ModItemRegistry = ModItemRegistry(this)

    val blockRegistry: ModBlockRegistry = ModBlockRegistry(this)

    val translationRegistry: ModTranslationRegistry = ModTranslationRegistry(this)

    val commandRegistry: ModCommandRegistry = ModCommandRegistry(this)

    val tagRegistry: ModTagRegistry = ModTagRegistry(this)

    val creativeModeTabRegistry: ModCreativeModeTabRegistry = ModCreativeModeTabRegistry(this)

    init {
        logger.info("$identifier is powered by starlight v. ${BuildConfig.STARLIGHT_VERSION}")

        ServerLifecycleEvents.SERVER_STARTED.register {
            val data = DataDrivenStarlight(this, it)
            onServerStart(data)
        }
    }

    abstract override fun onInitialize()

    open fun onServerStart(data: DataDrivenStarlight) {}

    fun debugger(name: String, callback: Debugger.DebuggerCallable) {
        Debugger.register(identifierOf(name), callback)
    }

    override fun hashCode(): Int {
        return Objects.hash(identifier)
    }

    override fun equals(other: Any?): Boolean {
        return if (other is StarlightModInitializer) identifier == other.identifier else false
    }

    fun identifierOf(value: String): Identifier {
        return Identifier.fromNamespaceAndPath(identifier, value)
    }

    fun texturePathOf(value: String): TexturePath {
        return TexturePath(identifierOf(value))
    }
}
