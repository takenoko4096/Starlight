package io.github.takenoko4096.starlight.registry.translation

import io.github.takenoko4096.starlight.StarlightModInitializer
import io.github.takenoko4096.starlight.registry.StarlightRegistry

class TranslationRegistry(mod: StarlightModInitializer) : StarlightRegistry(mod) {
    private val translations = mutableMapOf<String, TranslationConfiguration>()

    fun register(key: String, callback: TranslationConfiguration.() -> Unit) {
        if (translations.contains(key)) {
            throw IllegalArgumentException("翻訳キー '$key' は既に定義されているため登録できません")
        }

        val tc = TranslationConfiguration()
        tc.callback()
        translations[key] = tc
    }

    fun getTranslations(): Map<String, TranslationConfiguration> {
        return translations.toMap()
    }
}
