package io.github.takenoko4096.starlight.datagen

import io.github.takenoko4096.starlight.StarlightModInitializer
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput
import net.minecraft.core.HolderLookup
import java.util.concurrent.CompletableFuture

abstract class StarlightDataGenerator(private val mod: StarlightModInitializer) : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        val pack = fabricDataGenerator.createPack()

        pack.addProvider { output: FabricPackOutput ->
            StarlightModelProvider(mod, output)
        }

        pack.addProvider { output: FabricPackOutput, registryLookup: CompletableFuture<HolderLookup.Provider> ->
            StarlightLanguageProvider.EnUs(mod, output, registryLookup)
        }

        pack.addProvider { output: FabricPackOutput, registryLookup: CompletableFuture<HolderLookup.Provider> ->
            StarlightLanguageProvider.JaJp(mod, output, registryLookup)
        }

        pack.addProvider { output: FabricPackOutput, registryLookup: CompletableFuture<HolderLookup.Provider> ->
            StarlightItemTagsProvider(mod, output, registryLookup)
        }

        pack.addProvider { output: FabricPackOutput, registryLookup: CompletableFuture<HolderLookup.Provider> ->
            StarlightBlockTagsProvider(mod, output, registryLookup)
        }

        pack.addProvider { output: FabricPackOutput, registryLookup: CompletableFuture<HolderLookup.Provider> ->
            StarlightEntityTypeTagsProvider(mod, output, registryLookup)
        }

        pack.addProvider { output: FabricPackOutput, registryLookup: CompletableFuture<HolderLookup.Provider> ->
            StarlightBlockEntityTypeTagsProvider(mod, output, registryLookup)
        }

        onInitialize(pack)
    }

    open fun onInitialize(pack: FabricDataGenerator.Pack) {

    }
}
