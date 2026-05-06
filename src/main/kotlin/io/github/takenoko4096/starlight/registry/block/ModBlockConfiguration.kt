package io.github.takenoko4096.starlight.registry.block

import io.github.takenoko4096.starlight.StarlightDSL
import io.github.takenoko4096.starlight.registry.translation.ModTranslationConfiguration
import io.github.takenoko4096.starlight.render.model.block.PropertyVariants
import io.github.takenoko4096.starlight.render.model.item.builder.ItemModelHandle
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.data.BlockFamily
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.FenceBlock
import net.minecraft.world.level.block.FenceGateBlock
import net.minecraft.world.level.block.SlabBlock
import net.minecraft.world.level.block.StairBlock
import net.minecraft.world.level.block.WallBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.WoodType

@StarlightDSL
class ModBlockConfiguration(internal val registry: ModBlockRegistry, internal val identifier: String) {
    val blockResourceKey: ResourceKey<Block> = ResourceKey.create(
        Registries.BLOCK,
        Identifier.fromNamespaceAndPath(registry.mod.identifier, identifier)
    )

    val itemResourceKey: ResourceKey<Item> = ResourceKey.create(
        Registries.ITEM,
        Identifier.fromNamespaceAndPath(registry.mod.identifier, identifier)
    )

    internal var blockProperties: BlockBehaviour.Properties? = null

    internal var itemProperties: Item.Properties? = null

    internal var customBehaviourCreator: ((BlockBehaviour.Properties) -> Block) = { Block(it) }

    internal var renderingConfig: BlockRenderingConfiguration = BlockRenderingConfiguration(this)

    internal var translation = ModTranslationConfiguration()

    internal var withSlab: Boolean = false
    internal var withStairs: Boolean = false
    internal var withWall: Boolean = false
    internal var withFence: Boolean = false
    internal var withFenceGate: WoodType? = null

    internal var familyBuilder: BlockFamily.Builder? = null

    fun blockProperties(callback: BlockPropertiesConfiguration.() -> Unit) {
        val bpc = BlockPropertiesConfiguration(this, callback)
        blockProperties = bpc.build()
    }

    fun itemProperties(callback: BlockItemPropertiesConfiguration.() -> Unit) {
        val ipc = BlockItemPropertiesConfiguration(this, callback)
        itemProperties = ipc.build()
    }

    fun rendering(callback: BlockRenderingConfiguration.() -> Unit) {
        val brc = BlockRenderingConfiguration(this)
        brc.callback()
        renderingConfig = brc
        if (renderingConfig.modelConfig == null) {
            throw IllegalStateException("ブロック '${blockResourceKey.identifier()}' のモデル設定がありません")
        }
    }

    fun customBehaviour(callback: CustomBehaviourConfiguration.() -> Unit): CustomBehaviourInfo {
        val cbc = CustomBehaviourConfiguration()
        cbc.callback()
        customBehaviourCreator = cbc.build()
        return CustomBehaviourInfo(Properties(cbc.propertyDefinitions.toSet()))
    }

    fun translation(callback: ModTranslationConfiguration.() -> Unit) {
        val tc = ModTranslationConfiguration()
        tc.callback()
        translation = tc
    }

    fun withItem() {
        itemProperties {}
    }

    fun withSlab() {
        withSlab = true
    }

    fun withStairs() {
        withStairs = true
    }

    fun withWall() {
        withWall = true
    }

    fun withFence() {
        withFence = true
    }

    fun withFenceGate(woodType: WoodType = WoodType.OAK) {
        withFenceGate = woodType
    }

    internal fun register(): Block {
        if (blockProperties == null) {
            throw IllegalStateException("'blockProperties' is unset!")
        }

        val boundBlockProperties = blockProperties!!

        val fullBlock = withSlab || withStairs || withWall || withFence || (withFenceGate != null)

        if (fullBlock) {
            itemProperties {}
        }

        if (!fullBlock && renderingConfig.modelConfig?.blockModelConfig == null) {
            throw IllegalStateException("failed to register block '${blockResourceKey.identifier()}': 'rendering.models' is unset despite there are no withXX()")
        }
        else if (fullBlock && renderingConfig.modelConfig?.blockModelConfig != null) {
            throw IllegalStateException("failed to register block '${blockResourceKey.identifier()}': do not set 'rendering.models.block' while using withXX(): these options require parent block is a cubeAll() block")
        }

        val block = customBehaviourCreator(boundBlockProperties)
        Registry.register(BuiltInRegistries.BLOCK, blockResourceKey, block)

        if (itemProperties != null) {
            val item = BlockItem(block, itemProperties!!)
            Registry.register(BuiltInRegistries.ITEM, itemResourceKey, item)
        }

        if (withSlab) {
            registerFamilyMember(block, "slab", BlockFamily.Builder::slab, itemProperties!!) { a, b ->
                SlabBlock(b)
            }
        }

        if (withStairs) {
            registerFamilyMember(block, "stairs", BlockFamily.Builder::stairs, itemProperties!!) { a, b ->
                StairBlock(a.defaultBlockState(), b)
            }
        }

        if (withWall) {
            registerFamilyMember(block, "wall", BlockFamily.Builder::wall, itemProperties!!) { a, b ->
                WallBlock(b)
            }
        }

        if (withFence) {
            registerFamilyMember(block, "fence", BlockFamily.Builder::fence, itemProperties!!) { a, b ->
                FenceBlock(b)
            }
        }

        if (withFenceGate != null) {
            registerFamilyMember(block, "fence_gate", BlockFamily.Builder::fenceGate, itemProperties!!) { a, b ->
                val woodType = withFenceGate!!
                FenceGateBlock(woodType, b)
            }
        }

        return block
    }

    private fun <T : Block> registerFamilyMember(parent: Block, suffix: String, familyAppender: BlockFamily.Builder.(T) -> BlockFamily.Builder, itemProperties: Item.Properties, constructor: (Block, BlockBehaviour.Properties) -> T): T {
        val identifier = registry.mod.identifierOf(blockResourceKey.identifier().path + '_' + suffix)
        val blockKey = ResourceKey.create(Registries.BLOCK, identifier)
        val itemKey = ResourceKey.create(Registries.ITEM, identifier)

        val member = constructor(parent, BlockBehaviour.Properties.ofFullCopy(parent).setId(blockKey))
        Registry.register(BuiltInRegistries.BLOCK, blockKey, member)

        val translationKey = "block.${registry.mod.identifier}.${identifier.path}"
        registry.mod.translationRegistry.register(translationKey) {
            enUs = this@ModBlockConfiguration.translation.enUs + ' ' + if (suffix.length <= 1) suffix.uppercase() else suffix[0].uppercase() + suffix.slice(1..<suffix.length)
        }

        Registry.register(
            BuiltInRegistries.ITEM, itemKey,
            BlockItem(member, itemProperties.overrideDescription(translationKey))
        )

        if (familyBuilder == null) familyBuilder = BlockFamily.Builder(parent)
        familyBuilder!!.familyAppender(member)
        registry.blocks[blockKey] = member
        return member
    }

    class AccessorForClient internal constructor(private val configuration: ModBlockConfiguration) {
        fun blockModelLegacy(): BlockRenderingConfiguration.SingleArgBlockModel? {
            return configuration.renderingConfig.modelConfig?.blockModelConfig?.model
        }

        fun family(): BlockFamily? {
            return configuration.familyBuilder?.family
        }

        fun blockModelVariants(): PropertyVariants? {
            return configuration.renderingConfig.modelConfig?.blockModelConfig?.variants
        }

        fun blockItemModel(): ItemModelHandle? {
            return configuration.renderingConfig.modelConfig?.itemModelConfig?.handle
        }

        fun translation(): ModTranslationConfiguration {
            return configuration.translation
        }

        fun defaultTint(): (BlockState) -> Int {
            return configuration.renderingConfig.tintConfig.defaultColorGetter
        }

        fun inWorldTint(): (BlockState, BlockPos, Level) -> Int {
            return configuration.renderingConfig.tintConfig.colorGetter
        }

        fun terrainParticleTint(): (BlockState, BlockPos, Level) -> Int {
            return configuration.renderingConfig.tintConfig.particleColorGetter
        }
    }

    companion object {
        fun getAccessorForClient(configuration: ModBlockConfiguration): AccessorForClient {
            return AccessorForClient(configuration)
        }
    }
}
