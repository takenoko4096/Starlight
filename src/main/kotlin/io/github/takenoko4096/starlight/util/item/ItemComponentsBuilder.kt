package io.github.takenoko4096.starlight.util.item

import io.github.takenoko4096.starlight.StarlightDSL
import io.github.takenoko4096.starlight.StarlightModInitializer
import net.minecraft.core.RegistryAccess
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.Item

@StarlightDSL
open class ItemComponentsBuilder(private val mod: StarlightModInitializer, private val dataSource: RegistryAccess?, callback: ItemComponentsBuilder.() -> Unit) {
    private val components = mutableSetOf<AbstractItemComponent<*>>()

    fun build(target: Item.Properties) {
        components.forEach {
            it.set(target)
        }
    }

    fun attributeModifiers(callback: AttributeModifiersConfiguration.() -> Unit) {
        components.add(AttributeModifiersConfiguration(mod, callback).toComponent())
    }

    fun damage(value: Int) {
        components.add(AbstractItemComponent.valued(DataComponents.DAMAGE, value))
    }

    protected fun enchantments(callback: EnchantmentsConfiguration.() -> Unit) {
        if (dataSource == null) {
            throw IllegalStateException()
        }

        components.add(EnchantmentsConfiguration(mod, dataSource, callback).toComponent())
    }

    fun lore(callback: LoreConfiguration.() -> Unit) {
        components.add(LoreConfiguration(mod, callback).toComponent())
    }

    fun maxDamage(value: Int) {
        components.add(AbstractItemComponent.valued(DataComponents.MAX_DAMAGE, value))
    }

    fun maxStackSize(value: Int) {
        components.add(AbstractItemComponent.valued(DataComponents.MAX_STACK_SIZE, value))
    }

    fun rarity(callback: RarityConfiguration.() -> Unit) {
        components.add(RarityConfiguration(mod, callback).toComponent())
    }
}
