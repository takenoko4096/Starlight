package io.github.takenoko4096.starlight.datagen.model.builder

import io.github.takenoko4096.starlight.datagen.model.ClientModel
import io.github.takenoko4096.starlight.render.model.item.builder.End
import io.github.takenoko4096.starlight.render.model.item.builder.ItemModelBuilder
import io.github.takenoko4096.starlight.render.model.item.builder.ItemModelHandle
import io.github.takenoko4096.starlight.render.model.item.builder.select.BlockStateSelect
import io.github.takenoko4096.starlight.render.model.item.builder.select.ChargeTypeSelect
import io.github.takenoko4096.starlight.render.model.item.builder.select.ComponentSelect
import io.github.takenoko4096.starlight.render.model.item.builder.select.ContextDimensionSelect
import io.github.takenoko4096.starlight.render.model.item.builder.select.Select
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.client.renderer.item.SelectItemModel
import net.minecraft.client.renderer.item.properties.select.Charge
import net.minecraft.client.renderer.item.properties.select.ComponentContents
import net.minecraft.client.renderer.item.properties.select.ContextDimension
import net.minecraft.client.renderer.item.properties.select.ItemBlockState
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.CrossbowItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level

abstract class ClientItemModelHandle protected constructor(
    protected val itemModelGenerators: ItemModelGenerators,
    protected val item: Item,
    protected open val select: ItemModelHandle
) {
    abstract fun convert(): ItemModel.Unbaked

    fun register() {
        return itemModelGenerators.itemModelOutput.accept(item, convert())
    }

    class ClientEnd(itemModelGenerators: ItemModelGenerators, item: Item, private val end: End) : ClientItemModelHandle(itemModelGenerators, item, end) {
        override fun convert(): ItemModel.Unbaked {
            val model = ClientModel.getOrCreate(item, end.model, itemModelGenerators)
            return ItemModelUtils.plainModel(model.identifier)
        }
    }

    abstract class ClientSelect<T : Select<C>, C> protected constructor(itemModelGenerators: ItemModelGenerators, item: Item, override val select: T) : ClientItemModelHandle(itemModelGenerators, item, select) {
        protected fun <T> getCases(mapper: (C) -> T): List<SelectItemModel.SwitchCase<T>> {
            return ItemModelBuilder.AccessForClient.getSelectCases(select).map {
                ItemModelUtils.`when`(
                    it.`when`.map(mapper).toList(),
                    toClient(itemModelGenerators, item, select).convert()
                )
            }
        }

        protected fun getFallback(): ItemModel.Unbaked {
            return toClient(
                itemModelGenerators,
                item,
                ItemModelBuilder.AccessForClient.getSelectFallback(select)
            ).convert()
        }
    }

    class ClientBlockStateSelect<C : Comparable<C>>(itemModelGenerators: ItemModelGenerators, item: Item, select: BlockStateSelect<C>) : ClientSelect<BlockStateSelect<C>, C>(itemModelGenerators, item, select) {
        override fun convert(): ItemModel.Unbaked {
            return ItemModelUtils.select(
                ItemBlockState(select.property.name),
                getFallback(),
                getCases { v -> v.toString() }
            )
        }
    }

    class ClientChargeTypeSelect(itemModelGenerators: ItemModelGenerators, item: Item, select: ChargeTypeSelect) : ClientSelect<ChargeTypeSelect, CrossbowItem.ChargeType>(itemModelGenerators, item, select) {
        override fun convert(): ItemModel.Unbaked {
            return ItemModelUtils.select(
                Charge(),
                getFallback(),
                getCases { it }
            )
        }
    }

    class ClientComponentSelect<C>(itemModelGenerators: ItemModelGenerators, item: Item, select: ComponentSelect<C>) : ClientSelect<ComponentSelect<C>, C>(itemModelGenerators, item, select) {
        override fun convert(): ItemModel.Unbaked {
            return ItemModelUtils.select(
                ComponentContents(select.componentType),
                getFallback(),
                getCases { it }
            )
        }
    }

    class ClientContextDimensionSelect(itemModelGenerators: ItemModelGenerators, item: Item, select: ContextDimensionSelect) : ClientSelect<ContextDimensionSelect, ResourceKey<Level>>(itemModelGenerators, item, select) {
        override fun convert(): ItemModel.Unbaked {
            return ItemModelUtils.select(
                ContextDimension(),
                getFallback(),
                getCases { it }
            )
        }
    }

    companion object {
        private fun toClient(itemModelGenerators: ItemModelGenerators, item: Item, handle: ItemModelHandle): ClientItemModelHandle = when (handle) {
            is End -> ClientEnd(itemModelGenerators, item, handle)
            is BlockStateSelect<*> -> ClientBlockStateSelect(itemModelGenerators, item, handle)
            is ChargeTypeSelect -> ClientChargeTypeSelect(itemModelGenerators, item, handle)
            is ComponentSelect<*> -> ClientComponentSelect(itemModelGenerators, item, handle)
            is ContextDimensionSelect -> ClientContextDimensionSelect(itemModelGenerators, item, handle)
            else -> throw IllegalStateException()
        }
    }
}
