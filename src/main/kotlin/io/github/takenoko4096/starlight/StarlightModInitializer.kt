package io.github.takenoko4096.starlight

import io.github.takenoko4096.starlight.registry.block.ModBlockRegistry
import io.github.takenoko4096.starlight.registry.item.ModItemRegistry
import io.github.takenoko4096.starlight.registry.translation.ModTranslationRegistry
import net.fabricmc.api.ModInitializer
import net.minecraft.resources.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

abstract class StarlightModInitializer : ModInitializer {
    abstract val identifier: String

    val logger: Logger = LoggerFactory.getLogger(identifier)

    val itemRegistry: ModItemRegistry = ModItemRegistry(this)

    val blockRegistry: ModBlockRegistry = ModBlockRegistry(this)

    val translationRegistry: ModTranslationRegistry = ModTranslationRegistry(this)

    init {
        logger.info("$identifier is powered by Starlight")
    }

    abstract override fun onInitialize()

    override fun hashCode(): Int {
        return Objects.hash(identifier)
    }

    override fun equals(other: Any?): Boolean {
        return if (other is StarlightModInitializer) identifier == other.identifier else false
    }

    fun namespaced(value: String): Identifier {
        return Identifier.fromNamespaceAndPath(identifier, value)
    }
}
