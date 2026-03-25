package io.github.takenoko4096.starlight.render.model.block

class PropertyVariants0(internal var variant: NonClientBlockModelVariant? = null) : PropertyVariants() {
    fun NonClientBlockModelVariant.use() {
        variant = this
    }

    companion object {
        fun getVariant(p: PropertyVariants0): NonClientBlockModelVariant {
            return p.variant ?: throw IllegalStateException()
        }
    }
}
