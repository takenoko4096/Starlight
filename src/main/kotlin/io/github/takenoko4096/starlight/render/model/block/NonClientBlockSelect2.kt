package io.github.takenoko4096.starlight.render.model.block

class NonClientBlockSelect2<T : Comparable<T>, U : Comparable<U>>(
    val value1: T,
    val value2: U,
    variant: NonClientBlockModelVariant
) : NonClientBlockSelect(variant)
