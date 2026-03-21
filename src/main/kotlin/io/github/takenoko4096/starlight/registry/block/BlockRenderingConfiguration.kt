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

        val defaultTexturePath = TexturePath.defaultPath(configuration.resourceKey)

        val models: Models = Models(configuration.registry.mod, this)

        fun <T : Comparable<T>> propertyVariants(property: Property<T>, callback: VariantsByProperties1<T>.() -> Unit) {
            val vp1 = VariantsByProperties1(property)
            vp1.callback()
            variants = vp1
        }

        internal var itemModel: NonClientBlockModel? = null
    }

    abstract class BlockModel internal constructor()

    open class TexturePath private constructor(val identifier: Identifier) {
        fun suffixed(suffix: String): TexturePath {
            return TexturePath(identifier.withSuffix("_$suffix"))
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is TexturePath) return false

            if (identifier != other.identifier) return false

            return true
        }

        override fun hashCode(): Int {
            return identifier.hashCode()
        }

        companion object {
            internal fun defaultPath(resourceKey: ResourceKey<Block>): TexturePath {
                return TexturePath(resourceKey.identifier().withPrefix("block/"))
            }
        }
    }

    class Models internal constructor(private val mod: StarlightModInitializer, private val config: BlockModelConfiguration) {
        fun cubeDirectional(particle: TexturePath, north: TexturePath, south: TexturePath, east: TexturePath, west: TexturePath, up: TexturePath, down: TexturePath, callback: ModelSuffixProvider.() -> Unit = {}): NonClientBlockModel {
            return NonClientBlockModel(
                mod,
                config,
                ModelSuffixProvider(callback).suffix,
                "particle" to particle,
                "north" to north,
                "south" to south,
                "east" to east,
                "west" to west,
                "up" to up,
                "down" to down
            )
        }

        fun cubeAll(all: TexturePath, callback: ModelSuffixProvider.() -> Unit = {}): NonClientBlockModel {
            return NonClientBlockModel(
                mod,
                config,
                ModelSuffixProvider(callback).suffix,
                "all" to all
            )
        }
    }

    class ModelSuffixProvider internal constructor(callback: ModelSuffixProvider.() -> Unit) {
        var suffix: String = ""

        init {
            callback()
        }
    }

    class NonClientBlockModel(
        val mod: StarlightModInitializer,
        val blockModelConfiguration: BlockModelConfiguration,
        private val suffix: String,
        vararg paths: Pair<String, TexturePath>
    ) : BlockModel() {
        val mapping: Map<String, Identifier> = paths.associate { it.first to it.second.identifier }

        fun mutate(vararg mutators: NonClientVariantMutator): NonClientBlockModelVariant {
            return NonClientBlockModelVariant(this, mutators.toList())
        }

        fun plain(): NonClientBlockModelVariant {
            return NonClientBlockModelVariant(this, listOf())
        }

        fun setToDefaultItemModel() {
            blockModelConfiguration.itemModel = this
        }

        fun getSuffix(): String {
            return if (suffix.isEmpty()) "" else "_$suffix"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is NonClientBlockModel) return false

            if (mod != other.mod) return false
            if (mapping != other.mapping) return false

            return true
        }

        override fun hashCode(): Int {
            var result = mod.hashCode()
            result = 31 * result + mapping.hashCode()
            return result
        }
    }

    class NonClientBlockModelVariant internal constructor(
        val model: NonClientBlockModel,
        val mutators: List<NonClientVariantMutator>
    ) : BlockModel()

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

    abstract class NonClientSelect internal constructor(val modelVariant: NonClientBlockModelVariant)

    class NonClientSelect1<T : Comparable<T>> internal constructor(
        val value1: T,
        model: NonClientBlockModelVariant
    ) : NonClientSelect(model)

    class VariantsByProperties1<T : Comparable<T>> internal constructor(val property: Property<T>) : VariantsByProperties() {
        val selects: MutableSet<NonClientSelect1<T>> = mutableSetOf()

        fun NonClientBlockModelVariant.useWhen(value1: T) {
            selects.add(
                NonClientSelect1(
                    value1,
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
