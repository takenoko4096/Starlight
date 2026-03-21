package io.github.takenoko4096.starlight.client.datagen.model

import io.github.takenoko4096.starlight.registry.block.BlockRenderingConfiguration
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.ModelProvider
import net.minecraft.client.data.models.MultiVariant
import net.minecraft.client.data.models.model.ModelTemplate
import net.minecraft.client.data.models.model.TextureMapping
import net.minecraft.client.data.models.model.TextureSlot
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.Block
import java.util.*


class ClientBlockModel internal constructor(
    registrar: BlockModelVariantsRegistrar,
    nonClient: BlockRenderingConfiguration.NonClientBlockModel
) {
    internal val identifier: Identifier

    init {
        val map = nonClient.mapping.mapKeys { TextureSlot.create(it.key) }.mapValues { it.value }

        val template = ModelTemplate(
            Optional.of(
                Identifier.fromNamespaceAndPath(nonClient.mod.identifier, "block/")
            ),
            Optional.empty(),
            *map.keys.toTypedArray()
        )

        val mapping = TextureMapping().apply {
            map.forEach(::put)
        }

        identifier = template.createWithSuffix(
            registrar.block,
            nonClient.getSuffix(),
            mapping,
            registrar.blockModelGenerators.modelOutput
        )
    }

    internal fun toVariant(): MultiVariant {
        return BlockModelGenerators.plainVariant(identifier)
    }
}
