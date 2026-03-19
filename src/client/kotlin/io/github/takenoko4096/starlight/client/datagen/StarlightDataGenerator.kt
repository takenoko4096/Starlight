package io.github.takenoko4096.starlight.client.datagen

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.core.HolderLookup
import java.util.concurrent.CompletableFuture

abstract class StarlightDataGenerator : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        val pack = fabricDataGenerator.createPack()
        pack.addProvider { output: FabricDataOutput ->
            StarlightModelProvider(output)
        }
        pack.addProvider { output: FabricDataOutput, registryLookup: CompletableFuture<HolderLookup.Provider> ->
            StarlightJapaneseLanguageProvider(output, registryLookup)
        }
    }
}
