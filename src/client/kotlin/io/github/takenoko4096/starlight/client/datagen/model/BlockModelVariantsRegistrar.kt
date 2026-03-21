package io.github.takenoko4096.starlight.client.datagen.model

import io.github.takenoko4096.starlight.registry.block.BlockRenderingConfiguration
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.MultiVariant
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator
import net.minecraft.client.data.models.blockstates.PropertyDispatch
import net.minecraft.client.renderer.block.model.VariantMutator
import net.minecraft.world.level.block.Block

class BlockModelVariantsRegistrar internal constructor(
    internal val blockModelGenerators: BlockModelGenerators,
    internal val block: Block,
    internal val variants: BlockRenderingConfiguration.VariantsByProperties
) {
    private val empty = MultiVariantGenerator.dispatch(block)

    private fun createModelVariant(nonClient: BlockRenderingConfiguration.NonClientBlockModel): MultiVariant {
        return ClientBlockModel(this, nonClient).createModelVariant()
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
            var variant = createModelVariant(select.model)
            for (mutator in select.model.mutators) {
                variant = variant.with(toClientMutator(mutator))
            }

            dispatch.select(
                select.value1,
                variant
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
    }
}
