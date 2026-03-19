package io.github.takenoko4096.starlight.registry

import io.github.takenoko4096.starlight.StarlightModInitializer
import io.github.takenoko4096.starlight.registry.block.ModBlockRegistry

object StarlightRegistryAccess {
    private lateinit var mod: StarlightModInitializer

    private lateinit var blockRegistry: ModBlockRegistry

    fun initialize(mod: StarlightModInitializer) {
        this.mod = mod
        blockRegistry = ModBlockRegistry(mod)
    }

    fun getBlockRegistry(): ModBlockRegistry {
        return blockRegistry
    }
}
