package io.github.takenoko4096.starlight.client.datagen

import io.github.takenoko4096.starlight.StarlightModInitializer
import io.github.takenoko4096.starlight.registry.block.ModBlockConfiguration
import io.github.takenoko4096.starlight.registry.translation.TranslationConfiguration
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.core.HolderLookup
import java.util.concurrent.CompletableFuture

abstract class AbstractStarlightLanguageProvider(private val mod: StarlightModInitializer, output: FabricDataOutput, registryLookup: CompletableFuture<HolderLookup.Provider>, private val languageCode: String) : FabricLanguageProvider(output, languageCode, registryLookup) {
    protected abstract fun getTranslation(translationConfiguration: TranslationConfiguration): String?

    override fun generateTranslations(holderLookupProvider: HolderLookup.Provider, translationBuilder: TranslationBuilder) {
        val blockRegistry = mod.blockRegistry
        for (configuration in blockRegistry.getConfigurations()) {
            val block = blockRegistry.getBlock(configuration.resourceKey)
            val accessor = ModBlockConfiguration.getAccessorForClient(configuration)
            val translation = accessor.translation()

            getTranslation(translation)?.let {
                println(it)
                translationBuilder.add(block.descriptionId, it)
            }
        }

        val translationRegistry = mod.translationRegistry
        for (translation in translationRegistry.getTranslations()) {
            getTranslation(translation.value)?.let {
                translationBuilder.add(translation.key, it)
            }
        }
    }

    override fun getName(): String {
        return "StarlightLanguageProvider_$languageCode"
    }

    class EnUs(mod: StarlightModInitializer, output: FabricDataOutput, registryLookup: CompletableFuture<HolderLookup.Provider>) : AbstractStarlightLanguageProvider(mod, output, registryLookup, "en_us") {
        override fun getTranslation(translationConfiguration: TranslationConfiguration): String? {
            return translationConfiguration.enUs
        }
    }

    class JaJp(mod: StarlightModInitializer, output: FabricDataOutput, registryLookup: CompletableFuture<HolderLookup.Provider>) : AbstractStarlightLanguageProvider(mod, output, registryLookup, "ja_jp") {
        override fun getTranslation(translationConfiguration: TranslationConfiguration): String? {
            return translationConfiguration.jaJp
        }
    }
}
