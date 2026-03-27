package io.github.takenoko4096.starlight.util.item

import io.github.takenoko4096.starlight.StarlightDSL
import io.github.takenoko4096.starlight.StarlightModInitializer
import io.github.takenoko4096.starlight.util.item.ItemComponent
import io.github.takenoko4096.starlight.util.item.components.AttributeModifiersConfiguration
import io.github.takenoko4096.starlight.util.item.components.EnchantableConfiguration
import io.github.takenoko4096.starlight.util.item.components.EnchantmentsConfiguration
import io.github.takenoko4096.starlight.util.item.components.EquippableConfiguration
import io.github.takenoko4096.starlight.util.item.components.FoodConfiguration
import io.github.takenoko4096.starlight.util.item.components.LoreConfiguration
import io.github.takenoko4096.starlight.util.item.components.RarityConfiguration
import io.github.takenoko4096.starlight.util.item.components.RepairableConfiguration
import io.github.takenoko4096.starlight.util.item.components.SwingAnimationConfiguration
import io.github.takenoko4096.starlight.util.item.components.ToolConfiguration
import io.github.takenoko4096.starlight.util.item.components.WeaponConfiguration
import net.minecraft.core.RegistryAccess
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.enchantment.Enchantable

@StarlightDSL
open class ItemComponentsBuilder(private val mod: StarlightModInitializer, private val dataSource: RegistryAccess?, callback: ItemComponentsBuilder.() -> Unit) {
    private val components = mutableSetOf<ItemComponent<*>>()

    fun build(target: Item.Properties) {
        components.forEach {
            it.set(target)
        }
    }

    fun attributeModifiers(callback: AttributeModifiersConfiguration.() -> Unit) {
        components.add(AttributeModifiersConfiguration(mod, callback).toComponent())
    }

    fun damage(value: Int) {
        components.add(ItemComponent.valued(DataComponents.DAMAGE, value))
    }

    fun enchantable(callback: EnchantableConfiguration.() -> Unit) {
        components.add(EnchantableConfiguration(mod, callback).toComponent())
    }

    fun enchantmentGlintOverride(flag: Boolean) {
        components.add(ItemComponent.valued(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, flag))
    }

    protected fun enchantments(callback: EnchantmentsConfiguration.() -> Unit) {
        if (dataSource == null) {
            throw IllegalStateException()
        }

        components.add(EnchantmentsConfiguration(mod, dataSource, callback).toComponent())
    }

    fun equippable(callback: EquippableConfiguration.() -> Unit) {
        components.add(EquippableConfiguration(mod, callback).toComponent())
    }

    fun food(callback: FoodConfiguration.() -> Unit) {
        components.add(FoodConfiguration(mod, callback).toComponent())
    }

    fun glider() {
        components.add(ItemComponent.nonValued(DataComponents.GLIDER))
    }

    fun itemModel(identifier: Identifier) {
        components.add(ItemComponent.valued(DataComponents.ITEM_MODEL, identifier))
    }

    fun itemName(component: Component) {
        components.add(ItemComponent.valued(DataComponents.ITEM_NAME, component))
    }

    fun lore(callback: LoreConfiguration.() -> Unit) {
        components.add(LoreConfiguration(mod, callback).toComponent())
    }

    fun maxDamage(value: Int) {
        components.add(ItemComponent.valued(DataComponents.MAX_DAMAGE, value))
    }

    fun maxStackSize(value: Int) {
        components.add(ItemComponent.valued(DataComponents.MAX_STACK_SIZE, value))
    }

    fun minimumAttackCharge(requiredRate: Float) {
        components.add(ItemComponent.valued(DataComponents.MINIMUM_ATTACK_CHARGE, requiredRate))
    }

    fun rarity(callback: RarityConfiguration.() -> Unit) {
        components.add(RarityConfiguration(mod, callback).toComponent())
    }

    fun recipes(vararg recipes: ResourceKey<Recipe<*>>) {
        components.add(ItemComponent.valued(DataComponents.RECIPES, recipes.toList()))
    }

    fun repairCost(cost: Int) {
        components.add(ItemComponent.valued(DataComponents.REPAIR_COST, cost))
    }

    fun repairable(callback: RepairableConfiguration.() -> Unit) {
        components.add(RepairableConfiguration(mod, callback).toComponent())
    }

    fun swingAnimation(callback: SwingAnimationConfiguration.() -> Unit) {
        components.add(SwingAnimationConfiguration(mod, callback).toComponent())
    }

    fun tool(callback: ToolConfiguration.() -> Unit) {
        components.add(ToolConfiguration(mod, callback).toComponent())
    }

    fun unbreakable() {
        components.add(ItemComponent.nonValued(DataComponents.UNBREAKABLE))
    }

    fun weapon(callback: WeaponConfiguration.() -> Unit) {
        components.add(WeaponConfiguration(mod, callback).toComponent())
    }
}
