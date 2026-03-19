package io.github.takenoko4096.starlight.client.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.core.HolderLookup
import java.util.concurrent.CompletableFuture

class StarlightJapaneseLanguageProvider(output: FabricDataOutput, registryLookup: CompletableFuture<HolderLookup.Provider>) : FabricLanguageProvider(output, "ja_jp", registryLookup) {
    override fun generateTranslations(holderLookupProvider: HolderLookup.Provider, translationBuilder: TranslationBuilder) {
        translationBuilder.add(BlockRegistrar.WHITE_LEAVES, "白めの葉っぱ")
        translationBuilder.add(BlockRegistrar.METAL_BLOCK, "謎金属ブロック")
    }

    override fun getName(): String {
        return "StarlightJapaneseLanguageProvider"
    }
}
