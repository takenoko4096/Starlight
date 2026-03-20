package io.github.takenoko4096.starlight

import io.github.takenoko4096.starlight.registry.block.ModBlockRegistry
import net.fabricmc.api.ModInitializer

abstract class StarlightModInitializer : ModInitializer {
    abstract val identifier: String

    val blockRegistry: ModBlockRegistry = ModBlockRegistry(this)

    abstract override fun onInitialize()
}
