package io.github.takenoko4096.starlight.client.datagen

import io.github.takenoko4096.starlight.StarlightModInitializer
import net.minecraft.client.data.models.model.ModelTemplate
import net.minecraft.client.data.models.model.TextureSlot
import net.minecraft.resources.Identifier
import java.util.Optional

class CustomModelHelper(private val mod: StarlightModInitializer) {
    private fun modelTemplate(directory: String, variant: String?, vararg textureSlots: TextureSlot): ModelTemplate {
        return ModelTemplate(
            Optional.of(
                Identifier.fromNamespaceAndPath(mod.identifier, "$directory/")
            ),
            if (variant == null) Optional.empty() else Optional.of(variant),
            *textureSlots
        )
    }
}
