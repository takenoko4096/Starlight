package io.github.takenoko4096.starlight.client.datagen.model

import io.github.takenoko4096.starlight.registry.block.BlockRenderingConfiguration
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.MultiVariant
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator
import net.minecraft.client.data.models.blockstates.PropertyDispatch
import net.minecraft.client.data.models.model.ModelTemplate
import net.minecraft.client.data.models.model.TextureMapping
import net.minecraft.client.data.models.model.TextureSlot
import net.minecraft.resources.Identifier
import net.minecraft.world.level.block.Block
import java.util.*


class ClientBlockModel internal constructor(
    private val registrar: BlockModelVariantsRegistrar,
    nonClient: BlockRenderingConfiguration.NonClientBlockModel
) {
    private val template: ModelTemplate

    private val mapping: TextureMapping

    init {
        val map = nonClient.mapping.mapKeys { TextureSlot.create(it.key) }.mapValues { it.value }

        template = ModelTemplate(
            Optional.of(
                Identifier.fromNamespaceAndPath(nonClient.mod.identifier, "block/")
            ),
            Optional.empty(),
            *map.keys.toTypedArray()
        )

        mapping = TextureMapping().apply {
            map.forEach(::put)
        }
    }

    internal fun createModelVariant(): MultiVariant {
        val model = template.create(
            registrar.block,
            mapping,
            registrar.blockModelGenerators.modelOutput
        )

        registrar.blockModelGenerators.registerSimpleItemModel(registrar.block, model)

        return BlockModelGenerators.plainVariant(model)
    }
}
