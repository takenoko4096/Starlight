package io.github.takenoko4096.starlight.registry.block

import io.github.takenoko4096.starlight.StarlightDSL
import io.github.takenoko4096.starlight.render.TexturePath
import io.github.takenoko4096.starlight.render.model.NonClientModel
import io.github.takenoko4096.starlight.render.model.block.BlockModelProvider
import io.github.takenoko4096.starlight.render.model.block.PropertyVariants
import io.github.takenoko4096.starlight.render.model.block.PropertyVariants1
import io.github.takenoko4096.starlight.render.model.item.ItemModelProvider
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.ColorResolver
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.Property
import org.jetbrains.annotations.ApiStatus

@StarlightDSL
class BlockRenderingConfiguration internal constructor(private val configuration: ModBlockConfiguration) {
    internal var chunkSectionLayer: NonClientChunkSectionLayer = NonClientChunkSectionLayer.SOLID

    internal var modelConfig: ModelConfiguration = ModelConfiguration(configuration)

    internal var tintGetter: TintConfiguration.() -> Unit = {}

    fun layer(callback: LayerConfiguration.() -> Unit) {
        chunkSectionLayer = LayerConfiguration(callback).layer
    }

    fun models(callback: ModelConfiguration.() -> Unit) {
        val mc = ModelConfiguration(configuration)
        mc.callback()
        modelConfig = mc
    }

    fun tint(callback: TintConfiguration.() -> Unit) {
        tintGetter = callback
    }

    @StarlightDSL
    class LayerConfiguration internal constructor(callback: LayerConfiguration.() -> Unit) {
        internal var layer: NonClientChunkSectionLayer = NonClientChunkSectionLayer.SOLID

        init {
            callback()
        }

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
    class TintConfiguration internal constructor(
        val blockPos: BlockPos,
        val blockState: BlockState,
        val blockAndTintGetter: BlockAndTintGetter,
        val i: Int,
        callback: TintConfiguration.() -> Unit
    ) {
        var color: Int? = null

        init {
            callback()
        }
    }

    class LocatedBlockRenderView internal constructor(
        private val blockPos: BlockPos,
        private val blockState: BlockState,
        private val blockAndTintGetter: BlockAndTintGetter
    ) {
        fun getBiomeKey(): ResourceKey<Biome> {
            if (blockAndTintGetter.hasBiomes()) {
                val keyOptional = blockAndTintGetter.getBiomeFabric(blockPos).unwrapKey()
                if (keyOptional.isPresent) {
                    return keyOptional.get()
                }
                else {
                    throw IllegalStateException()
                }
            }

            throw IllegalStateException()
        }
    }

    @StarlightDSL
    class ModelConfiguration internal constructor(internal val configuration: ModBlockConfiguration) {
        internal var blockModelConfig = BlockModelConfiguration(configuration)

        internal var itemModelConfig = ItemModelConfiguration(configuration)

        val blockDefaultTexturePath = TexturePath.blockDefault(configuration.blockResourceKey)

        val itemDefaultTexturePath = TexturePath.itemDefault(configuration.itemResourceKey)

        val blockModels = BlockModelProvider(configuration.blockResourceKey)

        val itemModels = ItemModelProvider(configuration.itemResourceKey)

        fun block(callback: BlockModelConfiguration.() -> Unit) {
            val bmc = BlockModelConfiguration(configuration)
            bmc.callback()
            blockModelConfig = bmc
        }

        fun item(callback: ItemModelConfiguration.() -> Unit) {
            val imc = ItemModelConfiguration(configuration)
            imc.callback()
            itemModelConfig = imc
        }
    }

    @StarlightDSL
    class BlockModelConfiguration internal constructor(internal val configuration: ModBlockConfiguration) {
        internal var variants: PropertyVariants? = null

        fun <T : Comparable<T>> variants(property: Property<T>, callback: PropertyVariants1<T>.() -> Unit) {
            val vp1 = PropertyVariants1(property)
            vp1.callback()
            variants = vp1
        }

        /**
         * ↓のコードは PropertyVariants 自動操作に置き替える？ それとも削除？
         */
        internal var model: SingleArgBlockModel? = null

        private fun singleArg(textureMap: SingleArgBlockModel.SingleArgBlockTextureMap) {
            model = SingleArgBlockModel(textureMap)
        }

        @ApiStatus.Obsolete
        fun trivialCube() {
            singleArg(SingleArgBlockModel.SingleArgBlockTextureMap.TRIVIAL_CUBE)
        }

        @ApiStatus.Obsolete
        fun trivialColumn() {
            singleArg(SingleArgBlockModel.SingleArgBlockTextureMap.TRIVIAL_COLUMN)
        }

        @ApiStatus.Obsolete
        fun trivialColumnAlt() {
            singleArg(SingleArgBlockModel.SingleArgBlockTextureMap.TRIVIAL_COLUMN_ALT)
        }

        @ApiStatus.Obsolete
        fun trivialColumnHorizontal() {
            singleArg(SingleArgBlockModel.SingleArgBlockTextureMap.TRIVIAL_COLUMN_HORIZONTAL)
        }

        @ApiStatus.Obsolete
        fun trivialColumnHorizontalAlt() {
            singleArg(SingleArgBlockModel.SingleArgBlockTextureMap.TRIVIAL_COLUMN_HORIZONTAL_ALT)
        }

        @ApiStatus.Obsolete
        fun genericCube() {
            singleArg(SingleArgBlockModel.SingleArgBlockTextureMap.GENRIC_CUBE)
        }

        @ApiStatus.Obsolete
        fun anvil() {
            singleArg(SingleArgBlockModel.SingleArgBlockTextureMap.ANVIL)
        }

        @ApiStatus.Obsolete
        fun door() {
            singleArg(SingleArgBlockModel.SingleArgBlockTextureMap.DOOR)
        }

        @ApiStatus.Obsolete
        fun lantern() {
            singleArg(SingleArgBlockModel.SingleArgBlockTextureMap.LANTERN)
        }
    }

    class ItemModelConfiguration internal constructor(internal val configuration: ModBlockConfiguration) {
        internal var model: NonClientModel? = null

        fun NonClientModel.useAsItemModel() {
            model = this
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
