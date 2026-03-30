package io.github.takenoko4096.starlight

import io.github.takenoko4096.starlight.registry.ModCommandRegistry
import io.github.takenoko4096.starlight.registry.block.ModBlockRegistry
import io.github.takenoko4096.starlight.registry.item.ModItemRegistry
import io.github.takenoko4096.starlight.registry.translation.ModTranslationRegistry
import io.github.takenoko4096.starlight.render.TexturePath
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.resources.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

abstract class StarlightModInitializer(val identifier: String) : ModInitializer {
    val logger: Logger = LoggerFactory.getLogger(identifier)

    val itemRegistry: ModItemRegistry = ModItemRegistry(this)

    val blockRegistry: ModBlockRegistry = ModBlockRegistry(this)

    val translationRegistry: ModTranslationRegistry = ModTranslationRegistry(this)

    val modCommandRegistry: ModCommandRegistry = ModCommandRegistry(this)

    init {
        logger.info("$identifier is powered by Starlight")

        CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
            modCommandRegistry.use(dispatcher, registryAccess, environment)
        }
    }

    abstract override fun onInitialize()

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
