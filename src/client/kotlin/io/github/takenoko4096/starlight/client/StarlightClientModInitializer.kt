package io.github.takenoko4096.starlight.client

import io.github.takenoko4096.starlight.StarlightModInitializer
import io.github.takenoko4096.starlight.registry.command.ModCommandRegistry
import io.github.takenoko4096.starlight.registry.block.ModBlockConfiguration
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry
import net.minecraft.client.Minecraft
import net.minecraft.client.color.block.BlockTintSource
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.block.BlockAndTintGetter
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

abstract class StarlightClientModInitializer(private val mod: StarlightModInitializer) : ClientModInitializer {
    override fun onInitializeClient() {
        val blockRegistry = mod.blockRegistry

        for (configuration in blockRegistry.getConfigurations()) {
            val accessor = ModBlockConfiguration.getAccessorForClient(configuration)
            val block = blockRegistry.getBlock(configuration.blockResourceKey)

            // tint
            BlockColorRegistry.register(
                listOf(object : BlockTintSource {
                    override fun color(state: BlockState): Int {
                        return accessor.defaultTint()(state)
                    }

                    override fun colorInWorld(state: BlockState, level: BlockAndTintGetter, pos: BlockPos): Int {
                        return if (level is ClientLevel) {
                            accessor.inWorldTint()(state, pos, level)
                        }
                        else {
                            super.colorInWorld(state, level, pos)
                        }
                    }

                    override fun colorAsTerrainParticle(state: BlockState, level: BlockAndTintGetter, pos: BlockPos): Int {
                        return if (level is ClientLevel) {
                            accessor.terrainParticleTint()(state, pos, level)
                        }
                        else {
                            super.colorAsTerrainParticle(state, level, pos)
                        }
                    }
                }),
                block
            )
        }

        onInitialize()

        ClientLifecycleEvents.CLIENT_STARTED.register(::onClientStart)
    }

    open fun onInitialize() {}

    open fun onClientStart(client: Minecraft) {}
}
