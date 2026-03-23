package io.github.takenoko4096.starlight.datagen.model.builtin

import io.github.takenoko4096.starlight.render.model.builtin.NonClientBuiltinModelTemplate
import net.minecraft.client.data.models.model.ModelTemplate
import net.minecraft.client.data.models.model.ModelTemplates
import net.minecraft.client.data.models.model.TextureSlot

enum class ClientBuiltinModelTemplate(private val nonClient: NonClientBuiltinModelTemplate, private val client: ModelTemplate) {
    CUBE(NonClientBuiltinModelTemplate.CUBE, ModelTemplates.CUBE),
    CUBE_ALL(NonClientBuiltinModelTemplate.CUBE_ALL, ModelTemplates.CUBE_ALL);

    companion object {
        fun convert(nonClient: NonClientBuiltinModelTemplate): ModelTemplate {
            for (entry in entries) {
                if (entry.nonClient == nonClient) return entry.client
            }

            throw IllegalArgumentException("Not implemented (client side)")
        }
    }
}
