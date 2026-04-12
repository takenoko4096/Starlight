package io.github.takenoko4096.starlight.registry.translation

import io.github.takenoko4096.starlight.StarlightModInitializer
import io.github.takenoko4096.starlight.registry.StarlightRegistry

class ModTranslationRegistry(mod: StarlightModInitializer) : StarlightRegistry(mod) {
    private val translations = mutableMapOf<String, ModTranslationConfiguration>()

    fun register(key: String, callback: ModTranslationConfiguration.() -> Unit) {
        if (translations.contains(key)) {
            throw IllegalArgumentException("翻訳キー '$key' は既に定義されているため登録できません")
        }

        val tc = ModTranslationConfiguration()
        tc.callback()
        translations[key] = tc
    }

    fun getTranslations(): Map<String, ModTranslationConfiguration> {
        return translations.toMap()
    }
}
