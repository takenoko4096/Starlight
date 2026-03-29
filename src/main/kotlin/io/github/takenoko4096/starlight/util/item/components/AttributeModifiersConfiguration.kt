package io.github.takenoko4096.starlight.util.item.components

import io.github.takenoko4096.starlight.StarlightDSL
import io.github.takenoko4096.starlight.StarlightModInitializer
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponents
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.component.ItemAttributeModifiers

@StarlightDSL
class AttributeModifiersConfiguration internal constructor(mod: StarlightModInitializer, callback: AttributeModifiersConfiguration.() -> Unit) : AbstractComponentConfiguration<ItemAttributeModifiers>(mod, DataComponents.ATTRIBUTE_MODIFIERS) {
    private val entries = mutableListOf<ItemAttributeModifiers.Entry>()

    private var id: Int = 0

    init {
        callback()
    }

    private fun modifier(attribute: Holder<Attribute>, callback: ModifierEntryConfiguration.() -> Unit) {
        entries.add(ModifierEntryConfiguration(attribute, this, callback).build())
    }

    fun armor(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.ARMOR, callback)
    }

    fun armorToughness(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.ARMOR_TOUGHNESS, callback)
    }

    fun attackDamage(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.ATTACK_DAMAGE, callback)
    }

    fun attackKnockback(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.ATTACK_KNOCKBACK, callback)
    }

    fun attackSpeed(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.ATTACK_SPEED, callback)
    }

    fun blockBreakSpeed(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.BLOCK_BREAK_SPEED, callback)
    }

    fun blockInteractionRange(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.BLOCK_INTERACTION_RANGE, callback)
    }

    fun burningTime(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.BURNING_TIME, callback)
    }

    fun cameraDistance(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.CAMERA_DISTANCE, callback)
    }

    fun explosionKnockbackResistance(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE, callback)
    }

    fun entityInteractionRange(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.ENTITY_INTERACTION_RANGE, callback)
    }

    fun fallDamageMultiplier(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.FALL_DAMAGE_MULTIPLIER, callback)
    }

    fun flyingSpeed(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.FLYING_SPEED, callback)
    }

    fun followRange(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.FOLLOW_RANGE, callback)
    }

    fun gravity(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.GRAVITY, callback)
    }

    fun jumpStrength(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.JUMP_STRENGTH, callback)
    }

    fun knockbackResistance(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.KNOCKBACK_RESISTANCE, callback)
    }

    fun luck(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.LUCK, callback)
    }

    fun maxAbsorption(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.MAX_ABSORPTION, callback)
    }

    fun maxHealth(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.MAX_HEALTH, callback)
    }

    fun miningEfficiency(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.MINING_EFFICIENCY, callback)
    }

    fun movementEfficiency(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.MOVEMENT_EFFICIENCY, callback)
    }

    fun movementSpeed(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.MOVEMENT_SPEED, callback)
    }

    fun oxygenBonus(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.OXYGEN_BONUS, callback)
    }

    fun safeFallDistance(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.SAFE_FALL_DISTANCE, callback)
    }

    fun scale(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.SCALE, callback)
    }

    fun sneakingSpeed(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.SNEAKING_SPEED, callback)
    }

    fun spawnReinforcementsChance(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.SPAWN_REINFORCEMENTS_CHANCE, callback)
    }

    fun stepHeight(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.STEP_HEIGHT, callback)
    }

    fun submergedMiningSpeed(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.SUBMERGED_MINING_SPEED, callback)
    }

    fun sweepingDamageRatio(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.SWEEPING_DAMAGE_RATIO, callback)
    }

    fun temptRange(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.TEMPT_RANGE, callback)
    }

    fun waterMovementEfficiency(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.WATER_MOVEMENT_EFFICIENCY, callback)
    }

    fun waypointTransmitRange(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.WAYPOINT_TRANSMIT_RANGE, callback)
    }

    fun waypointReceiveRange(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.WAYPOINT_RECEIVE_RANGE, callback)
    }

    override fun build(): ItemAttributeModifiers {
        return ItemAttributeModifiers(entries.toList())
    }

    @StarlightDSL
    class ModifierEntryConfiguration(private val attribute: Holder<Attribute>, private val parent: AttributeModifiersConfiguration, callback: ModifierEntryConfiguration.() -> Unit) {
        private var identifier: Identifier = parent.mod.identifierOf((parent.id++).toString())

        private var operation: AttributeModifier.Operation = AttributeModifier.Operation.ADD_VALUE

        private var group = EquipmentSlotGroup.ANY

        var id: String
            get() = identifier.path
            set(value) {
                identifier = parent.mod.identifierOf(value)
            }

        var value: Double? = null

        init {
            callback()
        }

        fun operation(callback: OperationConfiguration.() -> Unit) {
            operation = OperationConfiguration(callback).operation
        }

        fun slot(callback: EquipmentSlotGroupConfiguration.() -> Unit) {
            group = EquipmentSlotGroupConfiguration(callback).group
        }

        fun build(): ItemAttributeModifiers.Entry {
            if (value == null) {
                throw IllegalStateException("Unset property 'value' in attribute modifier")
            }

            return ItemAttributeModifiers.Entry(
                attribute,
                AttributeModifier(
                    identifier,
                    value!!,
                    operation
                ),
                group
            )
        }

        @StarlightDSL
        class OperationConfiguration internal constructor(callback: OperationConfiguration.() -> Unit) {
            internal var operation = AttributeModifier.Operation.ADD_VALUE

            fun addValue() {
                operation = AttributeModifier.Operation.ADD_VALUE
            }

            fun addMultipliedBase() {
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
            }

            fun addMultipliedTotal() {
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
            }
        }

        @StarlightDSL
        class EquipmentSlotGroupConfiguration internal constructor(callback: EquipmentSlotGroupConfiguration.() -> Unit) {
            internal var group = EquipmentSlotGroup.ANY

            val armor = Armor(this)

            val weapon = Weapon(this)

            init {
                callback()
            }

            fun any() {
                group = EquipmentSlotGroup.ANY
            }

            fun saddle() {
                group = EquipmentSlotGroup.SADDLE
            }

            class Armor internal constructor(private val parent: EquipmentSlotGroupConfiguration) {
                fun any() {
                    parent.group = EquipmentSlotGroup.ARMOR
                }

                fun head() {
                    parent.group = EquipmentSlotGroup.HEAD
                }

                fun chest() {
                    parent.group = EquipmentSlotGroup.CHEST
                }

                fun legs() {
                    parent.group = EquipmentSlotGroup.LEGS
                }

                fun feet() {
                    parent.group = EquipmentSlotGroup.FEET
                }

                fun body() {
                    parent.group = EquipmentSlotGroup.BODY
                }
            }

            class Weapon internal constructor(private val parent: EquipmentSlotGroupConfiguration) {
                fun any() {
                    parent.group = EquipmentSlotGroup.HAND
                }

                fun mainhand() {
                    parent.group = EquipmentSlotGroup.MAINHAND
                }

                fun offhand() {
                    parent.group = EquipmentSlotGroup.OFFHAND
                }
            }
        }
    }
}