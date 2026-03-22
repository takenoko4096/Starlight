package io.github.takenoko4096.starlight.client.datagen.model

import io.github.takenoko4096.starlight.registry.block.BlockRenderingConfiguration
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.ModelProvider
import net.minecraft.client.data.models.MultiVariant
import net.minecraft.client.data.models.model.ModelTemplate
import net.minecraft.client.data.models.model.ModelTemplates
import net.minecraft.client.data.models.model.TextureMapping
import net.minecraft.client.data.models.model.TextureSlot
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.Block
import java.util.*
import io.github.takenoko4096.starlight.registry.block.BlockRenderingConfiguration.BuiltinNonClientModelTemplate
import io.github.takenoko4096.starlight.registry.block.BlockRenderingConfiguration.CustomNonClientModelTemplate

class ClientBlockModel internal constructor(
    registrar: BlockModelVariantsRegistrar,
    nonClient: BlockRenderingConfiguration.NonClientBlockModel
) {
    internal val identifier: Identifier

    init {
        val map = nonClient.mapping.mapKeys { TextureSlot.create(it.key) }.mapValues { it.value }

        val template = when (nonClient.parent) {
            is BuiltinNonClientModelTemplate -> {
                when (nonClient.parent) {
                    BuiltinNonClientModelTemplate.CUBE_DIRECTIONAL -> {
                        ModelTemplates.CUBE_DIRECTIONAL
                    }
                    BuiltinNonClientModelTemplate.CUBE_ALL -> {
                        ModelTemplates.CUBE_ALL
                    }
                    else -> {
                        throw IllegalStateException("NEVER HAPPENS")
                    }
                }
            }
            is CustomNonClientModelTemplate -> {
                ModelTemplate(
                    Optional.of(
                        (nonClient.parent as CustomNonClientModelTemplate).identifier
                    ),
                    Optional.empty(),
                    *map.keys.toTypedArray()
                )
            }
            else -> throw IllegalStateException("NEVER HAPPENS")
        }

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
