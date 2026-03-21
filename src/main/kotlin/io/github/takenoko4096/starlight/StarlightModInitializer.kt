package io.github.takenoko4096.starlight

import io.github.takenoko4096.starlight.registry.block.ModBlockRegistry
import io.github.takenoko4096.starlight.registry.translation.TranslationRegistry
import net.fabricmc.api.ModInitializer
import java.util.Objects

abstract class StarlightModInitializer : ModInitializer {
    abstract val identifier: String

    val blockRegistry: ModBlockRegistry = ModBlockRegistry(this)

    val translationRegistry: TranslationRegistry = TranslationRegistry(this)

    abstract override fun onInitialize()

    override fun hashCode(): Int {
        return Objects.hash(identifier)
    }

    override fun equals(other: Any?): Boolean {
        return if (other is StarlightModInitializer) identifier == other.identifier else false
    }
}
