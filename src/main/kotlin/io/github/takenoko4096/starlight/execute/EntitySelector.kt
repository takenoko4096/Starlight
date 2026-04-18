package io.github.takenoko4096.starlight.execute

import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.GameType

class EntitySelector(callback: EntitySelector.() -> Unit) {
    private val types = mutableSetOf<InvertibleValue<EntityType<*>>>()
    private val names = mutableSetOf<InvertibleValue<String>>()
    private val gameModes = mutableSetOf<InvertibleValue<GameType>>()
    private val tags = mutableSetOf<InvertibleValue<String>>()

    var x: Double? = null
    var y: Double? = null
    var z: Double? = null

    var distance: ClosedFloatingPointRange<Double>? = null

    var yRotation: ClosedFloatingPointRange<Float>? = null
    var xRotation: ClosedFloatingPointRange<Float>? = null

    var dx: Double? = null
    var dy: Double? = null
    var dz: Double? = null

    var limit: Int? = null

    var team: String? = null

    var sort: SelectorSortOrder = SelectorSortOrder.ARBITRARY

    var type: InvertibleValue<EntityType<*>>
        get() {
            throw IllegalSelectorArgumentException("このプロパティは getter が無効です")
        }
        set(value) {
            if (types.any { !it.not }) {
                throw IllegalSelectorArgumentException("この引数は既に単一のエンティティタイプが指定されています")
            }
            types.add(value)
        }

    var name: InvertibleValue<String>
        get() {
            throw IllegalSelectorArgumentException("このプロパティは getter が無効です")
        }
        set(value) {
            if (names.any { !it.not }) {
                throw IllegalSelectorArgumentException("この引数は既に単一の名前が指定されています")
            }
            names.add(value)
        }

    fun String.invertible(): InvertibleValue<String> = InvertibleValue.of(this)

    val acaciaBoat = InvertibleValue.entityType(EntityType.ACACIA_BOAT)
    val acaciaChestBoat = InvertibleValue.entityType(EntityType.ACACIA_CHEST_BOAT)
    val allay = InvertibleValue.entityType(EntityType.ALLAY)
    val areaEffectCloud = InvertibleValue.entityType(EntityType.ARMADILLO)
    val armorStand = InvertibleValue.entityType(EntityType.ARMOR_STAND)
    val arrow = InvertibleValue.entityType(EntityType.ARROW)
    val axolotl = InvertibleValue.entityType(EntityType.AXOLOTL)
    val bambooChestRaft = InvertibleValue.entityType(EntityType.BAMBOO_CHEST_RAFT)
    val bambooRaft = InvertibleValue.entityType(EntityType.BAMBOO_RAFT)
    val bat = InvertibleValue.entityType(EntityType.BAT)
    val bee = InvertibleValue.entityType(EntityType.BEE)
    val birchBoat = InvertibleValue.entityType(EntityType.BIRCH_BOAT)
    val birchChestBoat = InvertibleValue.entityType(EntityType.BIRCH_CHEST_BOAT)
    val blaze = InvertibleValue.entityType(EntityType.BLAZE)
    val blockDisplay = InvertibleValue.entityType(EntityType.BLOCK_DISPLAY)
    val bogged = InvertibleValue.entityType(EntityType.BOGGED)
    val breeze = InvertibleValue.entityType(EntityType.BREEZE)
    val breezeWindCharge = InvertibleValue.entityType(EntityType.BREEZE_WIND_CHARGE)
    val camel = InvertibleValue.entityType(EntityType.CAMEL)
    val camelHusk = InvertibleValue.entityType(EntityType.CAMEL_HUSK)
    val cat = InvertibleValue.entityType(EntityType.CAT)
    val caveSpider = InvertibleValue.entityType(EntityType.CAVE_SPIDER)

    init {
        callback()
    }

    companion object {
        fun main() {
            EntitySelector {
                type=acaciaBoat
                type=!allay
                type=armorStand
                type=!breezeWindCharge
                distance = 1.0..4.0
                yRotation = 1f..4f
                sort = SelectorSortOrder.NEAREST
                xRotation= 1.0.toFloat()..2.0f
                name = !"a".invertible()
            }
        }
    }

    private class IllegalSelectorArgumentException(message: String) : RuntimeException(message)
}
