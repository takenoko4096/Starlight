package io.github.takenoko4096.starlight.render.model.item.builder

import io.github.takenoko4096.starlight.StarlightDSL
import io.github.takenoko4096.starlight.render.model.NonClientModel
import io.github.takenoko4096.starlight.render.model.item.builder.condition.Condition
import io.github.takenoko4096.starlight.render.model.item.builder.select.Select
import io.github.takenoko4096.starlight.render.model.item.builder.select.SelectCase

@StarlightDSL
class ItemModelBuilder internal constructor(callback: ItemModelBuilder.() -> Unit) {
    internal var handle: ItemModelHandle? = null

    init {
        callback()
    }

    fun <T : Select<C>, C> select(select: T, callback: T.() -> Unit) {
        select.callback()
        handle = select
    }

    fun <T : Condition<C>, C> condition(condition: T, callback: T.() -> Unit) {
        condition.callback()
        handle = condition
    }

    fun model(model: NonClientModel) {
        handle = End(model)
    }

    fun build(): ItemModelHandle {
        return handle ?: throw IllegalStateException()
    }

    object AccessForClient {
        fun <C> getSelectCases(select: Select<C>): List<SelectCase<C>> {
            return select.cases.cases.toList()
        }

        fun <C> getSelectFallback(select: Select<C>): ItemModelHandle {
            return select.fallback.build()
        }
    }
}
