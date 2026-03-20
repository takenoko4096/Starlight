package io.github.takenoko4096.starlight.registry.block

import io.github.takenoko4096.starlight.StarlightDSL
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.Block

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
    class BlockModelConfiguration internal constructor(private val configuration: ModBlockConfiguration) {
        internal var model: BlockModel = SingleArgBlockModel(SingleArgBlockModel.SingleArgBlockTextureMap.TRIVIAL_CUBE)

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

        val models: Models = Models(TexturePath.defaultPath(configuration.resourceKey))

        fun variantByBlockStates() {

        }
    }

    abstract class BlockModel internal constructor()

    class TexturePath private constructor(val identifier: Identifier) {
        fun underScoredPrefix(prefix: String): TexturePath {
            return TexturePath(identifier.withPrefix("_$prefix"))
        }

        companion object {
            internal fun defaultPath(resourceKey: ResourceKey<Block>): TexturePath {
                return TexturePath(resourceKey.identifier().withPrefix("block/"))
            }
        }
    }

    class Models internal constructor(private val defaultTexturePath: TexturePath) {
        fun cubeDirectional(callback: CubeDirectionalBlockModel.() -> Unit): BlockModel {
            val model = CubeDirectionalBlockModel(defaultTexturePath)
            model.callback()
            model.run {
                if (arrayOf(particle, north, south, east, west, up, down).any { it == null }) {
                    throw IllegalStateException()
                }
            }
            return model
        }

        fun cubeAll(callback: CubeAllBlockModel.() -> Unit): BlockModel {
            val model = CubeAllBlockModel(defaultTexturePath)
            model.callback()
            model.run {
                if (all == null) {
                    throw IllegalStateException()
                }
            }
            return model
        }
    }

    class SingleArgBlockModel internal constructor(val textureMap: SingleArgBlockTextureMap) : BlockModel() {
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

    abstract class DefaultTexturePathProviderBlockModel(val defaultTexturePath: TexturePath) : BlockModel() {

    }

    class CubeDirectionalBlockModel internal constructor(defaultTexturePath: TexturePath) : DefaultTexturePathProviderBlockModel(defaultTexturePath) {
        var particle: TexturePath? = null
        var north: TexturePath? = null
        var south: TexturePath? = null
        var east: TexturePath? = null
        var west: TexturePath? = null
        var up: TexturePath? = null
        var down: TexturePath? = null
    }

    class CubeAllBlockModel internal constructor(defaultTexturePath: TexturePath) : DefaultTexturePathProviderBlockModel(defaultTexturePath) {
        var all: TexturePath? = null
    }
}
