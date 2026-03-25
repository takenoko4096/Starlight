package io.github.takenoko4096.starlight.datagen.model

import io.github.takenoko4096.starlight.render.model.NonClientModel
import io.github.takenoko4096.starlight.render.model.block.NonClientBlockModelVariant
import io.github.takenoko4096.starlight.render.model.block.NonClientVariantMutator
import io.github.takenoko4096.starlight.render.model.block.PropertyVariants
import io.github.takenoko4096.starlight.render.model.block.PropertyVariants0
import io.github.takenoko4096.starlight.render.model.block.PropertyVariants1
import io.github.takenoko4096.starlight.render.model.block.PropertyVariants2
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

    private fun toClient(nonClient: NonClientBlockModelVariant): MultiVariant {
        val clientModel = ClientModel.getOrCreate(block, nonClient.model, blockModelGenerators)
        var clientModelVariant = BlockModelGenerators.plainVariant(clientModel.identifier)
        for (mutator in nonClient.mutators) {
            clientModelVariant = clientModelVariant.with(toClientMutator(mutator))
        }
        return clientModelVariant
    }

    private fun variants0(variants0: PropertyVariants0): MultiVariantGenerator {
        return MultiVariantGenerator.dispatch(
            block,
            toClient(PropertyVariants0.getVariant(variants0))
        )
    }

    private fun <T : Comparable<T>> variants1(variants1: PropertyVariants1<T>): MultiVariantGenerator {
        val empty = MultiVariantGenerator.dispatch(block)

        val dispatch = PropertyDispatch.initial(variants1.property)

        for (select in variants1.selects) {
            dispatch.select(
                select.value1,
                toClient(select.variant)
            )
        }

        return empty.with(dispatch)
    }

    private fun <T : Comparable<T>, U : Comparable<U>> variants2(variants2: PropertyVariants2<T, U>): MultiVariantGenerator {
        val empty = MultiVariantGenerator.dispatch(block)

        val dispatch = PropertyDispatch.initial(variants2.property1, variants2.property2)

        for (select in variants2.selects) {
            dispatch.select(
                select.value1,
                select.value2,
                toClient(select.variant)
            )
        }

        return empty.with(dispatch)
    }

    internal fun register() {
        val generator = when (variants) {
            is PropertyVariants0 -> variants0(variants)
            is PropertyVariants1<*> -> variants1(variants)
            is PropertyVariants2<*, *> -> variants2(variants)
            else -> throw IllegalStateException()
        }

        blockModelGenerators.blockStateOutput.accept(generator)

        if (itemModel != null) {
            blockModelGenerators.registerSimpleItemModel(
                block,
                ClientModel.getOrCreate(block, itemModel, blockModelGenerators).identifier
            )
        }
    }
}
