package io.github.takenoko4096.starlight

import io.github.takenoko4096.starlight.registry.StarlightRegistryAccess
import net.fabricmc.api.ModInitializer

abstract class StarlightModInitializer : ModInitializer {
    abstract val identifier: String

    init {
        StarlightRegistryAccess.initialize(this)
    }

    abstract override fun onInitialize()
}
