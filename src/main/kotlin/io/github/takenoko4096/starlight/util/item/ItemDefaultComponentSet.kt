package io.github.takenoko4096.starlight.util.item

class ItemDefaultComponentSet internal constructor(private val callback: ItemComponents.() -> Unit) {
    internal fun apply(builder: ItemComponents) {
        callback(builder)
    }
}