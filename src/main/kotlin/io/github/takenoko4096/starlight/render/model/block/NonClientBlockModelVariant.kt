package io.github.takenoko4096.starlight.render.model.block

import io.github.takenoko4096.starlight.render.model.NonClientModel

class NonClientBlockModelVariant internal constructor(
    val model: NonClientModel,
    val mutators: List<NonClientVariantMutator>
)
