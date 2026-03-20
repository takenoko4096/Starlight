package io.github.takenoko4096.starlight.client.datagen

import io.github.takenoko4096.starlight.registry.StarlightRegistryAccess
import io.github.takenoko4096.starlight.registry.block.ModBlockConfiguration
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.core.HolderLookup
import java.util.concurrent.CompletableFuture

abstract class AbstractStarlightLanguageProvider(output: FabricDataOutput, registryLookup: CompletableFuture<HolderLookup.Provider>) : FabricLanguageProvider(output, "ja_jp", registryLookup) {
    protected abstract fun getLanguage(translationConfiguration: ModBlockConfiguration.TranslationConfiguration): String?

    override fun generateTranslations(holderLookupProvider: HolderLookup.Provider, translationBuilder: TranslationBuilder) {
        val blockRegistry = StarlightRegistryAccess.getBlockRegistry()
        for (configuration in blockRegistry.getConfigurations()) {
            val block = blockRegistry.getBlock(configuration.resourceKey)
            val accessor = ModBlockConfiguration.getAccessorForClient(configuration)
            val translation = accessor.translation()

            getLanguage(translation)?.let {
                translationBuilder.add(block.descriptionId, it)
            }
        }
    }

    override fun getName(): String {
        return "StarlightLanguageProvider"
    }

    class EnUs(output: FabricDataOutput, registryLookup: CompletableFuture<HolderLookup.Provider>) : AbstractStarlightLanguageProvider(output, registryLookup) {
        override fun getLanguage(translationConfiguration: ModBlockConfiguration.TranslationConfiguration): String? {
            return translationConfiguration.enUs
        }
    }

    class JaJp(output: FabricDataOutput, registryLookup: CompletableFuture<HolderLookup.Provider>) : AbstractStarlightLanguageProvider(output, registryLookup) {
        override fun getLanguage(translationConfiguration: ModBlockConfiguration.TranslationConfiguration): String? {
            return translationConfiguration.jaJp
        }
    }
}
