package io.github.takenoko4096.starlight.registry.block

import io.github.takenoko4096.starlight.StarlightDSL
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
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
import net.minecraft.world.level.storage.loot.LootTable
import java.util.Optional
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

    fun customBehaviour(callback: CustomBehaviourConfiguration.() -> Unit) {
        val cbc = CustomBehaviourConfiguration()
        cbc.callback()
        customBehaviourCreator = cbc.build()
    }

    internal fun register(): Block {
        if (blockProperties == null) throw IllegalStateException()

        val block = Blocks.register(
            resourceKey,
            customBehaviourCreator,
            blockProperties
        )

        if (itemProperties != null) {
            Items.registerBlock(block, itemProperties)
        }

        return block
    }

    @StarlightDSL
    class BlockPropertiesConfiguration internal constructor(private val configuration: ModBlockConfiguration, callback: BlockPropertiesConfiguration.() -> Unit) {
        init {
            callback()
        }

        var strength: Float = 1f

        var sound: SoundType = SoundType.STONE

        var occlusion = true

        var collision = true

        var lootTable: ResourceKey<LootTable>? = null

        var isLiquid = false

        var isReplaceable = false

        var requiresCorrectToolForDrops = false

        var explosionResistance: Float = 1f

        internal fun build(): BlockBehaviour.Properties {
            val properties = BlockBehaviour.Properties.of()
            properties.strength(strength)
            properties.sound(sound)
            if (!occlusion) properties.noOcclusion()
            if (!collision) properties.noCollision()
            if (lootTable != null) properties.overrideLootTable(Optional.of(lootTable!!))
            if (isLiquid) properties.liquid()
            if (isReplaceable) properties.replaceable()
            if (requiresCorrectToolForDrops) properties.requiresCorrectToolForDrops()
            properties.explosionResistance(explosionResistance)
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
            else properties.overrideDescription(translationKey)
            return properties
        }
    }

    @StarlightDSL
    class CustomBehaviourConfiguration {
        var propertyDefinitions: Set<BlockStatesConfiguration.PropertyDefinition<*>> = setOf()

        var eventHandlers: Set<BlockEventsConfiguration.BlockEventHandler<*, *>> = setOf()

        fun blockStates(callback: BlockStatesConfiguration.() -> Unit) {
            val bsc = BlockStatesConfiguration()
            bsc.callback()
            propertyDefinitions = bsc.build()
        }

        fun events(callback: BlockEventsConfiguration.() -> Unit) {
            val bec = BlockEventsConfiguration()
            bec.callback()
            eventHandlers = bec.build()
        }

        internal fun build(): (BlockBehaviour.Properties) -> CustomBlock {
            return {
                CustomBlock(it, propertyDefinitions, eventHandlers)
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

        fun <T> enumerationProperty(name: String, callback: EnumerationPropertyConfiguration<T>.() -> Unit) where T : Enum<T>, T : StringRepresentable {
            if (states.any { it.property.name == name }) {
                throw IllegalStateException()
            }

            val config = EnumerationPropertyConfiguration<T>(name)
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

        class EnumerationPropertyConfiguration<T> internal constructor(name: String) : PropertyConfiguration<T>(name) where T : Enum<T>, T : StringRepresentable {
            var clazz: KClass<T>? = null

            override fun build(): PropertyDefinition<T> {
                if (defaultValue == null) {
                    throw IllegalStateException()
                }
                else if (clazz == null) {
                    throw IllegalStateException()
                }

                return PropertyDefinition(
                    EnumProperty.create(name, clazz!!.java),
                    defaultValue!!
                )
            }
        }

        class PropertyDefinition<T : Comparable<T>> internal constructor(
            val property: Property<T>,
            val defaultValue: T
        )
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

        abstract class BlockEvent internal constructor() {}

        class BlockEventHandler<T : BlockEvent, R> internal constructor(
            val clazz: KClass<T>,
            val callback: (T) -> R
        )

        class FallOnEvent internal constructor(
            val level: Level,
            val blockState: BlockState,
            val blockPos: BlockPos,
            val entity: Entity,
            val distance: Double,
            var causeDamage: Boolean = true
        ) : BlockEvent()

        internal fun build(): Set<BlockEventHandler<*, *>> {
            return handlers.toSet()
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
            internal var model: BlockModel = TrivialBlockModel(TrivialBlockModel.TrivialBlockTextureMap.CUBE)

            fun trivial(textureMap: TrivialBlockModel.TrivialBlockTextureMap) {
                model = TrivialBlockModel(textureMap)
            }

            fun trivialCube() {
                trivial(TrivialBlockModel.TrivialBlockTextureMap.CUBE)
            }
        }

        abstract class BlockModel internal constructor()

        class TrivialBlockModel internal constructor(val textureMap: TrivialBlockTextureMap) : BlockModel() {
            enum class TrivialBlockTextureMap {
                CUBE
            }
        }
    }

    open class CustomBlock internal constructor(
        properties: Properties,
        private val propertyDefinitions: Set<BlockStatesConfiguration.PropertyDefinition<*>>,
        private val eventHandlers: Set<BlockEventsConfiguration.BlockEventHandler<*, *>>
    ) : Block(properties) {
        init {
            val defaultState = defaultBlockState()

            for (definition in propertyDefinitions) {
                defaultState.setValue(definition.property, definition.defaultValue)
            }

            registerDefaultState(defaultState)
        }

        override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
            for (definition in propertyDefinitions) {
                builder.add(definition.property)
            }
        }

        override fun fallOn(level: Level, blockState: BlockState, blockPos: BlockPos, entity: Entity, d: Double) {
            val event = BlockEventsConfiguration.FallOnEvent(level, blockState, blockPos, entity, d)

            eventHandlers.filter { it.clazz == BlockEventsConfiguration.FallOnEvent::class }
                .forEach {
                    (it.callback as (BlockEventsConfiguration.FallOnEvent) -> Unit)(event)
                }

            if (event.causeDamage) {
                super.fallOn(level, blockState, blockPos, entity, d)
            }
        }
    }
}
