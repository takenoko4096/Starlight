package io.github.takenoko4096.starlight.registry.block

import io.github.takenoko4096.starlight.StarlightDSL
import io.github.takenoko4096.starlight.StarlightModInitializer
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.Block
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
    class BlockModelConfiguration internal constructor(private val configuration: ModBlockConfiguration) {
        internal var model: BlockModel? = null

        internal var variants: VariantsByProperties? = null

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

        val models: Models = Models(configuration.registry.mod, TexturePath.defaultPath(configuration.resourceKey))

        fun <T : Comparable<T>> propertyVariants(property: Property<T>, callback: VariantsByProperties1<T>.() -> Unit) {
            val vp1 = VariantsByProperties1(property)
            vp1.callback()
            variants = vp1
        }
    }

    abstract class BlockModel internal constructor()

    open class TexturePath private constructor(val identifier: Identifier) {
        fun underScoredPrefix(prefix: String): TexturePath {
            return TexturePath(identifier.withPrefix("_$prefix"))
        }

        companion object {
            internal fun defaultPath(resourceKey: ResourceKey<Block>): TexturePath {
                return TexturePath(resourceKey.identifier().withPrefix("block/"))
            }
        }
    }

    class Models internal constructor(private val mod: StarlightModInitializer, val defaultTexturePath: TexturePath) {
        fun cubeDirectional(particle: TexturePath, north: TexturePath, south: TexturePath, east: TexturePath, west: TexturePath, up: TexturePath, down: TexturePath): BlockModel {
            return NonClientBlockModel(
                mod,
                listOf(),
                "particle" to particle,
                "north" to north,
                "south" to south,
                "east" to east,
                "west" to west,
                "up" to up,
                "down" to down
            )
        }

        fun cubeAll(all: TexturePath): BlockModel {
            return NonClientBlockModel(
                mod,
                listOf(),
                "all" to all
            )
        }
    }

    class NonClientBlockModel(
        val mod: StarlightModInitializer,
        val mutators: List<NonClientVariantMutator>,
        vararg paths: Pair<String, TexturePath>
    ) : BlockModel() {
        val mapping: Map<String, Identifier> = paths.associate { it.first to it.second.identifier }
    }

    enum class NonClientVariantMutator {
        X_ROT_90,
        X_ROT_180,
        X_ROT_270,
        Y_ROT_90,
        Y_ROT_180,
        Y_ROT_270,
        UV_LOCK
    }

    abstract class VariantsByProperties {
        //
    }

    abstract class NonClientSelect internal constructor(val model: NonClientBlockModel)

    class NonClientSelect1<T : Comparable<T>> internal constructor(
        val value1: T,
        model: NonClientBlockModel
    ) : NonClientSelect(model)

    class VariantsByProperties1<T : Comparable<T>> internal constructor(val property: Property<T>) : VariantsByProperties() {
        val selects: MutableSet<NonClientSelect1<T>> = mutableSetOf()

        fun NonClientBlockModel.useWhen(value: T) {
            selects.add(
                NonClientSelect1(
                    value,
                    this
                )
            )
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
}
