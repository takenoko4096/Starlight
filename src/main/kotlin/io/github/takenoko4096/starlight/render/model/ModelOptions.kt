package io.github.takenoko4096.starlight.render.model

import io.github.takenoko4096.starlight.StarlightDSL

@StarlightDSL
class ModelOptions internal constructor(callback: ModelOptions.() -> Unit) {
    var suffix: String? = null

    init {
        callback()
    }
}
