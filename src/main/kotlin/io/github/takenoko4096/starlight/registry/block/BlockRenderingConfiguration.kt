package io.github.takenoko4096.starlight.registry.block

import io.github.takenoko4096.starlight.StarlightDSL
import io.github.takenoko4096.starlight.render.TexturePath
import io.github.takenoko4096.starlight.render.model.ModelOptions
import io.github.takenoko4096.starlight.render.model.NonClientModel
import io.github.takenoko4096.starlight.render.model.builtin.NonClientBuiltinModelTemplate
import io.github.takenoko4096.starlight.render.model.block.PropertyVariants
import io.github.takenoko4096.starlight.render.model.block.PropertyVariants1
import io.github.takenoko4096.starlight.render.model.builtin.NonClientBuiltinModel
import io.github.takenoko4096.starlight.render.model.builtin.NonClientBuiltinTextureSlot
import io.github.takenoko4096.starlight.render.model.custom.NonClientCustomModel
import net.minecraft.resources.Identifier
import net.minecraft.world.level.block.state.properties.Property

@StarlightDSL
class BlockRenderingConfiguration internal constructor(private val configuration: ModBlockConfiguration) {
    internal var chunkSectionLayer: NonClientChunkSectionLayer = NonClientChunkSectionLayer.SOLID

    internal var blockModelConfig = BlockModelConfiguration(configuration)

    fun chunkSectionLayer(callback: LayerConfiguration.() -> Unit) {
        val lc = LayerConfiguration()
        lc.callback()
        chunkSectionLayer = lc.layer
    }

    fun blockModel(callback: BlockModelConfiguration.() -> Unit) {
        val bmc = BlockModelConfiguration(configuration)
        bmc.callback()
        blockModelConfig = bmc
    }

    @StarlightDSL
    class LayerConfiguration internal constructor() {
        internal var layer: NonClientChunkSectionLayer = NonClientChunkSectionLayer.SOLID

        fun solid() {
            layer = NonClientChunkSectionLayer.SOLID
        }

        fun cutout() {
            layer = NonClientChunkSectionLayer.CUTOUT
        }

        fun translucent() {
            layer = NonClientChunkSectionLayer.TRANSLUCENT
        }

        fun tripwire() {
            layer = NonClientChunkSectionLayer.TRIPWIRE
        }
    }

    enum class NonClientChunkSectionLayer {
        SOLID,
        CUTOUT,
        TRANSLUCENT,
        TRIPWIRE
    }

    @StarlightDSL
    class BlockModelConfiguration internal constructor(internal val configuration: ModBlockConfiguration) {
        internal var variants: PropertyVariants? = null

        internal var item: NonClientModel? = null

        val defaultTexturePath = TexturePath.blockDefault(configuration.resourceKey)

        val models: BlockModelProvider = BlockModelProvider(this)

        fun <T : Comparable<T>> variants(property: Property<T>, callback: PropertyVariants1<T>.() -> Unit) {
            val vp1 = PropertyVariants1(property)
            vp1.callback()
            variants = vp1
        }

        fun NonClientModel.useAsItemModel() {
            item = this
        }

        internal var model: SingleArgBlockModel? = null

        private fun singleArg(textureMap: SingleArgBlockModel.SingleArgBlockTextureMap) {
            model = SingleArgBlockModel(textureMap)
        }

        fun trivialCube() {
            singleArg(SingleArgBlockModel.SingleArgBlockTextureMap.TRIVIAL_CUBE)
        }

        fun trivialColumn() {
            singleArg(SingleArgBlockModel.SingleArgBlockTextureMap.TRIVIAL_COLUMN)
        }

        fun trivialColumnAlt() {
            singleArg(SingleArgBlockModel.SingleArgBlockTextureMap.TRIVIAL_COLUMN_ALT)
        }

        fun trivialColumnHorizontal() {
            singleArg(SingleArgBlockModel.SingleArgBlockTextureMap.TRIVIAL_COLUMN_HORIZONTAL)
        }

        fun trivialColumnHorizontalAlt() {
            singleArg(SingleArgBlockModel.SingleArgBlockTextureMap.TRIVIAL_COLUMN_HORIZONTAL_ALT)
        }

        fun genericCube() {
            singleArg(SingleArgBlockModel.SingleArgBlockTextureMap.GENRIC_CUBE)
        }

        fun anvil() {
            singleArg(SingleArgBlockModel.SingleArgBlockTextureMap.ANVIL)
        }

        fun door() {
            singleArg(SingleArgBlockModel.SingleArgBlockTextureMap.DOOR)
        }

        fun lantern() {
            singleArg(SingleArgBlockModel.SingleArgBlockTextureMap.LANTERN)
        }
    }

    class BlockModelProvider internal constructor(private val configuration: BlockModelConfiguration) {
        fun cube(particle: TexturePath, north: TexturePath, south: TexturePath, east: TexturePath, west: TexturePath, up: TexturePath, down: TexturePath, callback: ModelOptions.() -> Unit = {}): NonClientBuiltinModel {
            return NonClientBuiltinModel(
                configuration.configuration.resourceKey,
                NonClientBuiltinModelTemplate.CUBE,
                mapOf(
                    NonClientBuiltinTextureSlot.PARTICLE to particle,
                    NonClientBuiltinTextureSlot.NORTH to north,
                    NonClientBuiltinTextureSlot.SOUTH to south,
                    NonClientBuiltinTextureSlot.EAST to east,
                    NonClientBuiltinTextureSlot.WEST to west,
                    NonClientBuiltinTextureSlot.UP to up,
                    NonClientBuiltinTextureSlot.DOWN to down
                ),
                ModelOptions(callback)
            )
        }

        fun cubeAll(all: TexturePath, callback: ModelOptions.() -> Unit = {}): NonClientBuiltinModel {
            return NonClientBuiltinModel(
                configuration.configuration.resourceKey,
                NonClientBuiltinModelTemplate.CUBE_ALL,
                mapOf(
                    NonClientBuiltinTextureSlot.ALL to all
                ),
                ModelOptions(callback)
            )
        }

        fun custom(modelTemplate: Identifier, textureMapping: Map<String, TexturePath>, callback: ModelOptions.() -> Unit = {}): NonClientCustomModel {
            return NonClientCustomModel(
                configuration.configuration.resourceKey,
                modelTemplate,
                textureMapping,
                ModelOptions(callback)
            )
        }
    }

    class SingleArgBlockModel internal constructor(val textureMap: SingleArgBlockTextureMap) {
        enum class SingleArgBlockTextureMap {
            TRIVIAL_CUBE,
            TRIVIAL_COLUMN,
            TRIVIAL_COLUMN_ALT,
            TRIVIAL_COLUMN_HORIZONTAL,
            TRIVIAL_COLUMN_HORIZONTAL_ALT,
            GENRIC_CUBE,
            ANVIL,
            DOOR,
            LANTERN
        }
    }
}
