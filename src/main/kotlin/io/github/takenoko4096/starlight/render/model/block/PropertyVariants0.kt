package io.github.takenoko4096.starlight.render.model.block

class PropertyVariants0(var variant: NonClientBlockModelVariant? = null) : PropertyVariants() {
    fun NonClientBlockModelVariant.useAsBlockModel() {
        variant = this
    }
}
