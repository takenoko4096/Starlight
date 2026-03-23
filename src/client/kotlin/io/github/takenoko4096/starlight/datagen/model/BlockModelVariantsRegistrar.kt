package io.github.takenoko4096.starlight.datagen.model

import io.github.takenoko4096.starlight.render.model.NonClientModel
import io.github.takenoko4096.starlight.render.model.block.NonClientVariantMutator
import io.github.takenoko4096.starlight.render.model.block.PropertyVariants
import io.github.takenoko4096.starlight.render.model.block.PropertyVariants1
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.MultiVariant
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator
import net.minecraft.client.data.models.blockstates.PropertyDispatch
import net.minecraft.client.renderer.block.model.VariantMutator
import net.minecraft.world.level.block.Block

class BlockModelVariantsRegistrar internal constructor(
    internal val blockModelGenerators: BlockModelGenerators,
    internal val block: Block,
    internal val itemModel: NonClientModel?,
    internal val variants: PropertyVariants
) {
    private val empty = MultiVariantGenerator.dispatch(block)

    private fun toClientMutator(nonClientMutator: NonClientVariantMutator): VariantMutator {
        return when (nonClientMutator) {
            NonClientVariantMutator.X_ROT_90 -> BlockModelGenerators.X_ROT_90
            NonClientVariantMutator.X_ROT_180 -> BlockModelGenerators.X_ROT_180
            NonClientVariantMutator.X_ROT_270 -> BlockModelGenerators.X_ROT_270
            NonClientVariantMutator.Y_ROT_90 -> BlockModelGenerators.Y_ROT_90
            NonClientVariantMutator.Y_ROT_180 -> BlockModelGenerators.Y_ROT_180
            NonClientVariantMutator.Y_ROT_270 -> BlockModelGenerators.Y_ROT_270
            NonClientVariantMutator.UV_LOCK -> BlockModelGenerators.UV_LOCK
        }
    }

    private fun <T : Comparable<T>> variants1(variants1: PropertyVariants1<T>): PropertyDispatch.C1<MultiVariant, T> {
        val dispatch = PropertyDispatch.initial(variants1.property)

        for (select in variants1.selects) {
            val clientModel = ClientModel.getOrCreate(block, select.variant.model, blockModelGenerators)
            var clientModelVariant = BlockModelGenerators.plainVariant(clientModel.identifier)
            for (mutator in select.variant.mutators) {
                clientModelVariant = clientModelVariant.with(toClientMutator(mutator))
            }

            dispatch.select(
                select.value1,
                clientModelVariant
            )
        }

        return dispatch
    }

    internal fun register() {
        val dispatch = when (variants) {
            is PropertyVariants1<*> -> variants1(variants)
            else -> throw IllegalStateException()
        }

        val generator = empty.with(dispatch)
        blockModelGenerators.blockStateOutput.accept(generator)

        if (itemModel != null) {
            blockModelGenerators.registerSimpleItemModel(
                block,
                ClientModel.getOrCreate(block, itemModel, blockModelGenerators).identifier
            )
        }
    }
}
