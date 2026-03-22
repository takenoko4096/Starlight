package io.github.takenoko4096.starlight.client.datagen

import io.github.takenoko4096.starlight.StarlightModInitializer
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.core.HolderLookup
import java.util.concurrent.CompletableFuture

abstract class StarlightDataGenerator(private val mod: StarlightModInitializer) : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        val pack = fabricDataGenerator.createPack()

        pack.addProvider { output: FabricDataOutput ->
            StarlightModelProvider(mod, output)
        }

        pack.addProvider { output: FabricDataOutput, registryLookup: CompletableFuture<HolderLookup.Provider> ->
            AbstractStarlightLanguageProvider.EnUs(mod, output, registryLookup)
        }

        pack.addProvider { output: FabricDataOutput, registryLookup: CompletableFuture<HolderLookup.Provider> ->
            AbstractStarlightLanguageProvider.JaJp(mod, output, registryLookup)
        }

        onInitialize(pack)
    }

    open fun onInitialize(pack: FabricDataGenerator.Pack) {

    }
}
