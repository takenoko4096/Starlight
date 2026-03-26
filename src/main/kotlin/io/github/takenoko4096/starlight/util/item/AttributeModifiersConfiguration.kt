package io.github.takenoko4096.starlight.util.item

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
class AttributeModifiersConfiguration internal constructor(mod: StarlightModInitializer, callback: AttributeModifiersConfiguration.() -> Unit) : ComponentConfiguration<ItemAttributeModifiers>(mod, DataComponents.ATTRIBUTE_MODIFIERS) {
    private val entries = mutableListOf<ItemAttributeModifiers.Entry>()

    private var id: Int = 0

    init {
        callback()
    }

    private fun modifier(attribute: Holder<Attribute>, callback: ModifierEntryConfiguration.() -> Unit) {
        entries.add(
            ModifierEntryConfiguration(attribute, this, callback).build()
        )
    }

    fun armor(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.ARMOR, callback)
    }

    fun armorToughness(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.ARMOR_TOUGHNESS, callback)
    }

    override fun build(): ItemAttributeModifiers {
        return ItemAttributeModifiers(listOf(

        ))
    }

    @StarlightDSL
    class ModifierEntryConfiguration(private val attribute: Holder<Attribute>, private val parent: AttributeModifiersConfiguration, callback: ModifierEntryConfiguration.() -> Unit) {
        private var identifier: Identifier = parent.mod.namespaced((parent.id++).toString())

        private var operation: AttributeModifier.Operation = AttributeModifier.Operation.ADD_VALUE

        private var group = EquipmentSlotGroup.ANY

        var id: String
            get() = identifier.path
            set(value) {
                identifier = parent.mod.namespaced(value)
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
