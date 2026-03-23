package io.github.takenoko4096.starlight.render.model.builtin

enum class NonClientBuiltinModelTemplate(vararg val textureSlots: NonClientBuiltinTextureSlot) {
    CUBE(
        NonClientBuiltinTextureSlot.PARTICLE,
        NonClientBuiltinTextureSlot.NORTH,
        NonClientBuiltinTextureSlot.SOUTH,
        NonClientBuiltinTextureSlot.EAST,
        NonClientBuiltinTextureSlot.WEST,
        NonClientBuiltinTextureSlot.UP,
        NonClientBuiltinTextureSlot.DOWN
    ),
    CUBE_ALL(NonClientBuiltinTextureSlot.ALL);
}
