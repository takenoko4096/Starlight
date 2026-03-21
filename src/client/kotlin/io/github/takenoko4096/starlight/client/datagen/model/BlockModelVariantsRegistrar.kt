package io.github.takenoko4096.starlight.client.datagen.model

import io.github.takenoko4096.starlight.registry.block.BlockRenderingConfiguration
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.MultiVariant
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator
import net.minecraft.client.data.models.blockstates.PropertyDispatch
import net.minecraft.client.renderer.block.model.VariantMutator
import net.minecraft.resources.Identifier
import net.minecraft.world.level.block.Block

class BlockModelVariantsRegistrar internal constructor(
    internal val blockModelGenerators: BlockModelGenerators,
    internal val block: Block,
    internal val defaultItemModel: BlockRenderingConfiguration.NonClientBlockModel?,
    internal val variants: BlockRenderingConfiguration.VariantsByProperties
) {
    private val empty = MultiVariantGenerator.dispatch(block)

    private val createdModels = mutableMapOf<BlockRenderingConfiguration.NonClientBlockModel, Identifier>()

    private fun getOrCreateModel(nonClient: BlockRenderingConfiguration.NonClientBlockModel): Identifier {
        if (createdModels.contains(nonClient)) {
            return createdModels[nonClient]!!
        }
        else {
            createdModels[nonClient] = ClientBlockModel(this, nonClient).identifier
            return createdModels[nonClient]!!
        }
    }

    private fun toClientMutator(nonClientMutator: BlockRenderingConfiguration.NonClientVariantMutator): VariantMutator {
        return when (nonClientMutator) {
            BlockRenderingConfiguration.NonClientVariantMutator.X_ROT_90 -> BlockModelGenerators.X_ROT_90
            BlockRenderingConfiguration.NonClientVariantMutator.X_ROT_180 -> BlockModelGenerators.X_ROT_180
            BlockRenderingConfiguration.NonClientVariantMutator.X_ROT_270 -> BlockModelGenerators.X_ROT_270
            BlockRenderingConfiguration.NonClientVariantMutator.Y_ROT_90 -> BlockModelGenerators.Y_ROT_90
            BlockRenderingConfiguration.NonClientVariantMutator.Y_ROT_180 -> BlockModelGenerators.Y_ROT_180
            BlockRenderingConfiguration.NonClientVariantMutator.Y_ROT_270 -> BlockModelGenerators.Y_ROT_270
            BlockRenderingConfiguration.NonClientVariantMutator.UV_LOCK -> BlockModelGenerators.UV_LOCK
        }
    }

    private fun <T : Comparable<T>> variants1(variants1: BlockRenderingConfiguration.VariantsByProperties1<T>): PropertyDispatch.C1<MultiVariant, T> {
        val dispatch = PropertyDispatch.initial(variants1.property)

        for (select in variants1.selects) {
            var clientModel = BlockModelGenerators.plainVariant(getOrCreateModel(select.modelVariant.model))
            for (mutator in select.modelVariant.mutators) {
                clientModel = clientModel.with(toClientMutator(mutator))
            }

            dispatch.select(
                select.value1,
                clientModel
            )
        }

        return dispatch
    }

    internal fun register() {
        val dispatch = when (variants) {
            is BlockRenderingConfiguration.VariantsByProperties1<*> -> variants1(variants)
            else -> throw IllegalStateException()
        }

        val generator = empty.with(dispatch)
        blockModelGenerators.blockStateOutput.accept(generator)

        if (defaultItemModel != null && createdModels.contains(defaultItemModel)) {
            val identifier = createdModels[defaultItemModel]!!
            blockModelGenerators.registerSimpleItemModel(block, identifier)
        }
    }
}
