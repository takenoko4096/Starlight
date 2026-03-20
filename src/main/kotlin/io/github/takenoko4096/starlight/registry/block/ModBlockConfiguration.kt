package io.github.takenoko4096.starlight.registry.block

import io.github.takenoko4096.starlight.StarlightDSL
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.level.block.state.properties.Property
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.material.PushReaction
import net.minecraft.world.level.storage.loot.LootTable
import java.util.*
import java.util.function.BiConsumer
import kotlin.reflect.KClass

@StarlightDSL
class ModBlockConfiguration(private val registry: ModBlockRegistry, private val identifier: String) {
    val resourceKey: ResourceKey<Block> = ResourceKey.create(
        Registries.BLOCK,
        Identifier.fromNamespaceAndPath(registry.mod.identifier, identifier)
    )

    internal var blockProperties: BlockBehaviour.Properties? = null

    internal var itemProperties: Item.Properties? = null

    internal var customBehaviourCreator: ((BlockBehaviour.Properties) -> Block) = { Block(it) }

    internal var renderingConfig: BlockRenderingConfiguration = BlockRenderingConfiguration()

    internal var translation = TranslationConfiguration()

    fun blockProperties(callback: BlockPropertiesConfiguration.() -> Unit) {
        val bpc = BlockPropertiesConfiguration(this, callback)
        blockProperties = bpc.build()
    }

    fun itemProperties(callback: ItemPropertiesConfiguration.() -> Unit) {
        val ipc = ItemPropertiesConfiguration(this, callback)
        itemProperties = ipc.build()
    }

    fun rendering(callback: BlockRenderingConfiguration.() -> Unit) {
        val brc = BlockRenderingConfiguration()
        brc.callback()
        renderingConfig = brc
    }

    fun customBehaviour(callback: CustomBehaviourConfiguration.() -> Unit): CustomBehaviourInfo {
        val cbc = CustomBehaviourConfiguration()
        cbc.callback()
        customBehaviourCreator = cbc.build()
        return CustomBehaviourInfo(Properties(cbc.propertyDefinitions.toSet()))
    }

    fun translation(callback: TranslationConfiguration.() -> Unit) {
        val tc = TranslationConfiguration()
        tc.callback()
        translation = tc
    }

    internal fun register(): Block {
        if (blockProperties == null) throw IllegalStateException()

        val block = Blocks.register(
            resourceKey,
            customBehaviourCreator,
            blockProperties!!
        )

        if (itemProperties != null) {
            Items.registerBlock(block, itemProperties!!)
        }

        return block
    }

    @StarlightDSL
    class BlockPropertiesConfiguration internal constructor(private val configuration: ModBlockConfiguration, callback: BlockPropertiesConfiguration.() -> Unit) {
        init {
            callback()
        }

        var destroyTime: Float = 1f

        var sound: SoundType = SoundType.STONE

        var occlusion = true

        var collision = true

        var friction = 0.6f

        var speedFactor = 1f

        var jumpFactor = 1f

        var pushReaction: PushReaction = PushReaction.NORMAL

        var lootTable: ResourceKey<LootTable>? = null

        var isLiquid = false

        var isReplaceable = false

        var requiresCorrectToolForDrops = false

        var explosionResistance: Float = 1f

        private var mapColorProvider: (BlockState) -> MapColor = { MapColor.STONE }

        private var lightLevelProvider: (BlockState) -> Int = { 0 }

        fun mapColor(callback: (BlockState) -> MapColor) {
            mapColorProvider = callback
        }

        fun lightLevel(callback: (BlockState) -> Int) {
            lightLevelProvider = callback
        }

        internal fun build(): BlockBehaviour.Properties {
            val properties = BlockBehaviour.Properties.of()
            properties.destroyTime(destroyTime)
            properties.sound(sound)
            if (!occlusion) properties.noOcclusion()
            if (!collision) properties.noCollision()
            if (lootTable != null) properties.overrideLootTable(Optional.of(lootTable!!))
            if (isLiquid) properties.liquid()
            if (isReplaceable) properties.replaceable()
            if (requiresCorrectToolForDrops) properties.requiresCorrectToolForDrops()
            properties.explosionResistance(explosionResistance)
            properties.friction(friction)
            properties.speedFactor(speedFactor)
            properties.jumpFactor(jumpFactor)
            properties.pushReaction(pushReaction)
            properties.mapColor(mapColorProvider)
            properties.lightLevel(lightLevelProvider)
            return properties
        }
    }

    @StarlightDSL
    class ItemPropertiesConfiguration internal constructor(private val configuration: ModBlockConfiguration, callback: ItemPropertiesConfiguration.() -> Unit) {
        init {
            callback()
        }

        private var translationKey: String? = null

        fun translationKeyOf(value: String) {
            translationKey = "block.${configuration.registry.mod.identifier}.$value"
        }

        fun translationKeyAuto() {
            translationKeyOf(configuration.identifier)
        }

        internal fun build(): Item.Properties {
            val properties = Item.Properties()
            if (translationKey == null) throw IllegalStateException()
            else properties.overrideDescription(translationKey!!)
            return properties
        }
    }

    @StarlightDSL
    class CustomBehaviourConfiguration {
        internal var propertyDefinitions: Set<BlockStatesConfiguration.PropertyDefinition<*>> = setOf()

        private var eventDispatcher: BlockEventsConfiguration.BlockEventDispatcher = BlockEventsConfiguration.BlockEventDispatcher(setOf())

        fun blockStates(callback: BlockStatesConfiguration.() -> Unit): Properties {
            val bsc = BlockStatesConfiguration()
            bsc.callback()
            propertyDefinitions = bsc.build()
            return Properties(propertyDefinitions.toSet())
        }

        fun events(callback: BlockEventsConfiguration.() -> Unit) {
            val bec = BlockEventsConfiguration()
            bec.callback()
            eventDispatcher = bec.build()
        }

        internal fun build(): (BlockBehaviour.Properties) -> CustomBlock {
            return {
                CustomBlock(it, propertyDefinitions, eventDispatcher)
            }
        }
    }

    @StarlightDSL
    class BlockStatesConfiguration internal constructor() {
        private val states = mutableSetOf<PropertyDefinition<*>>()

        fun booleanProperty(name: String, callback: PropertyConfiguration<Boolean>.() -> Unit) {
            if (states.any { it.property.name == name }) {
                throw IllegalStateException()
            }

            val config = BooleanPropertyConfiguration(name)
            config.callback()
            states.add(config.build())
        }

        fun integerProperty(name: String, callback: IntegerPropertyConfiguration.() -> Unit) {
            if (states.any { it.property.name == name }) {
                throw IllegalStateException()
            }

            val config = IntegerPropertyConfiguration(name)
            config.callback()
            states.add(config.build())
        }

        fun <T> enumerationProperty(name: String, clazz: KClass<T>, callback: EnumerationPropertyConfiguration<T>.() -> Unit) where T : Enum<T>, T : StringRepresentable {
            if (states.any { it.property.name == name }) {
                throw IllegalStateException()
            }

            val config = EnumerationPropertyConfiguration(name, clazz)
            config.callback()
            states.add(config.build())
        }

        internal fun build(): Set<PropertyDefinition<*>> {
            return states.toSet()
        }

        abstract class PropertyConfiguration<T : Comparable<T>> internal constructor(protected val name: String) {
            var defaultValue: T? = null

            abstract fun build(): PropertyDefinition<T>
        }

        class BooleanPropertyConfiguration internal constructor(name: String) : PropertyConfiguration<Boolean>(name) {
            override fun build(): PropertyDefinition<Boolean> {
                if (defaultValue == null) {
                    throw IllegalStateException()
                }

                return PropertyDefinition(
                    BooleanProperty.create(name),
                    defaultValue!!
                )
            }
        }

        class IntegerPropertyConfiguration internal constructor(name: String) : PropertyConfiguration<Int>(name) {
            var range: IntRange? = null

            override fun build(): PropertyDefinition<Int> {
                if (defaultValue == null) {
                    throw IllegalStateException()
                }
                else if (range == null) {
                    throw IllegalStateException()
                }

                return PropertyDefinition(
                    IntegerProperty.create(name, range!!.first, range!!.last),
                    defaultValue!!
                )
            }
        }

        class EnumerationPropertyConfiguration<T> internal constructor(name: String, private val clazz: KClass<T>) : PropertyConfiguration<T>(name) where T : Enum<T>, T : StringRepresentable {
            override fun build(): PropertyDefinition<T> {
                if (defaultValue == null) {
                    throw IllegalStateException()
                }

                return PropertyDefinition(
                    EnumProperty.create(name, clazz.java),
                    defaultValue!!
                )
            }
        }

        class PropertyDefinition<T : Comparable<T>> internal constructor(
            val property: Property<T>,
            val defaultValue: T
        ) {
            internal fun setDefaultValueTo(blockState: BlockState) {
                blockState.setValue(property, defaultValue)
            }
        }
    }

    @StarlightDSL
    class BlockEventsConfiguration internal constructor() {
        private val handlers = mutableSetOf<BlockEventHandler<*, *>>()

        fun onFallOn(callback: FallOnEvent.() -> Unit) {
            handlers.add(BlockEventHandler(
                FallOnEvent::class,
                callback
            ))
        }

        fun onStepOn(callback: StepOnEvent.() -> Unit) {
            handlers.add(BlockEventHandler(
                StepOnEvent::class,
                callback
            ))
        }

        fun onExplosionHit(callback: ExplosionHitEvent.() -> Unit) {
            handlers.add(BlockEventHandler(
                ExplosionHitEvent::class,
                callback
            ))
        }

        abstract class BlockEvent<R> internal constructor() {}

        class BlockEventHandler<T : BlockEvent<R>, R> internal constructor(
            val clazz: KClass<T>,
            val callback: T.() -> R
        )

        class BlockEventDispatcher internal constructor(private val set: Set<BlockEventHandler<*, *>>) {
            internal fun <T : BlockEvent<R>, R> dispatch(clazz: KClass<T>, event: T) {
                set.filter { it.clazz == clazz }
                    .forEach { (it.callback as T.() -> R)(event) }
            }
        }

        class FallOnEvent internal constructor(
            val level: Level,
            val blockState: BlockState,
            val blockPos: BlockPos,
            val entity: Entity,
            var distance: Double,
            var causeDamage: Boolean = true
        ) : BlockEvent<Unit>()

        class StepOnEvent internal constructor(
            val level: Level,
            val blockState: BlockState,
            val blockPos: BlockPos,
            val entity: Entity
        ) : BlockEvent<Unit>()

        class ExplosionHitEvent internal constructor(
            val serverLevel: ServerLevel,
            val blockState: BlockState,
            val blockPos: BlockPos,
            val explosion: Explosion,
            var ignore: Boolean = false,
            var dropHandle: (ExplosionDrop) -> Unit
        ) : BlockEvent<Unit>() {
            class ExplosionDrop internal constructor(
                val itemStack: ItemStack,
                val blockPos: BlockPos
            )
        }

        internal fun build(): BlockEventDispatcher {
            return BlockEventDispatcher(handlers.toSet())
        }
    }

    @StarlightDSL
    class BlockRenderingConfiguration internal constructor() {
        internal var chunkSectionLayer: NonClientChunkSectionLayer = NonClientChunkSectionLayer.SOLID

        internal var blockModelConfig = BlockModelConfiguration()

        fun chunkSectionLayer(callback: LayerConfiguration.() -> Unit) {
            val lc = LayerConfiguration()
            lc.callback()
            chunkSectionLayer = lc.layer
        }

        fun blockModel(callback: BlockModelConfiguration.() -> Unit) {
            val bmc = BlockModelConfiguration()
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
        class BlockModelConfiguration internal constructor() {
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
        }

        abstract class BlockModel internal constructor()

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

    open class CustomBlock internal constructor(
        properties: Properties,
        private val propertyDefinitions: Set<BlockStatesConfiguration.PropertyDefinition<*>>,
        private val eventDispatcher: BlockEventsConfiguration.BlockEventDispatcher
    ) : Block(properties) {
        init {
            val defaultState = defaultBlockState()

            for (definition in propertyDefinitions) {
                definition.setDefaultValueTo(defaultState)
            }

            registerDefaultState(defaultState)
        }

        override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
            for (definition in propertyDefinitions) {
                builder.add(definition.property)
            }
        }

        override fun stepOn(level: Level, blockPos: BlockPos, blockState: BlockState, entity: Entity) {
            val event = BlockEventsConfiguration.StepOnEvent(level, blockState, blockPos, entity)
            eventDispatcher.dispatch(BlockEventsConfiguration.StepOnEvent::class, event)
        }

        override fun fallOn(level: Level, blockState: BlockState, blockPos: BlockPos, entity: Entity, d: Double) {
            val event = BlockEventsConfiguration.FallOnEvent(level, blockState, blockPos, entity, d)
            eventDispatcher.dispatch(BlockEventsConfiguration.FallOnEvent::class, event)

            if (event.causeDamage) {
                super.fallOn(level, blockState, blockPos, entity, event.distance)
            }
        }

        override fun onExplosionHit(blockState: BlockState, serverLevel: ServerLevel, blockPos: BlockPos, explosion: Explosion, biConsumer: BiConsumer<ItemStack, BlockPos>) {
            val event = BlockEventsConfiguration.ExplosionHitEvent(serverLevel, blockState, blockPos, explosion, false) {
                biConsumer.accept(it.itemStack, it.blockPos)
            }

            if (!event.ignore) {
                super.onExplosionHit(blockState, serverLevel, blockPos, explosion) { itemStack, blockPos ->
                    event.dropHandle(BlockEventsConfiguration.ExplosionHitEvent.ExplosionDrop(itemStack, blockPos))
                }
            }
        }
    }

    @StarlightDSL
    class TranslationConfiguration internal constructor() {
        var enUs: String? = null

        var jaJp: String? = null
    }

    class Properties internal constructor(private val definitions: Set<BlockStatesConfiguration.PropertyDefinition<*>>) {
        private fun getProperty(name: String): Property<*> {
            val def = definitions.find { it.property.name == name }
            if (def == null) throw IllegalArgumentException()
            return def.property
        }

        fun boolean(name: String): BooleanProperty {
            return getProperty(name) as? BooleanProperty ?: throw IllegalArgumentException()
        }

        fun integer(name: String): IntegerProperty {
            return getProperty(name) as? IntegerProperty ?: throw IllegalArgumentException()
        }

        fun <T> enumeration(name: String, clazz: KClass<T>): EnumProperty<T> where T : Enum<T>, T : StringRepresentable {
            val property = getProperty(name) as? EnumProperty<*> ?: throw IllegalArgumentException()
            if (property.valueClass == clazz.java) {
                return property as EnumProperty<T>
            }
            else {
                throw IllegalArgumentException()
            }
        }
    }

    class CustomBehaviourInfo internal constructor(
        val properties: Properties
    )

    class AccessorForClient internal constructor(private val configuration: ModBlockConfiguration) {
        fun chunkSectionLayer(): BlockRenderingConfiguration.NonClientChunkSectionLayer {
            return configuration.renderingConfig.chunkSectionLayer
        }

        fun blockModel(): BlockRenderingConfiguration.BlockModel {
            return configuration.renderingConfig.blockModelConfig.model
        }

        fun translation(): TranslationConfiguration {
            return configuration.translation
        }
    }

    companion object {
        fun getAccessorForClient(configuration: ModBlockConfiguration): AccessorForClient {
            return AccessorForClient(configuration)
        }
    }
}
