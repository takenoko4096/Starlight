package io.github.takenoko4096.starlight.registry.tag

import io.github.takenoko4096.starlight.StarlightModInitializer
import io.github.takenoko4096.starlight.registry.StarlightRegistry
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType

class ModTagRegistry internal constructor(mod: StarlightModInitializer) : StarlightRegistry(mod) {
    private val configurations = mutableSetOf<ModTagConfiguration<*>>()

    private val tags = mutableMapOf<TagKey<*>, Tag<*>>()

    fun registerOfItem(tag: TagKey<Item>, configuration: ModTagConfiguration<Item>.() -> Unit) {
        val configuration = ModTagConfiguration(Registries.ITEM, tag, configuration)
        configurations.add(configuration)
        tags[tag] = configuration.build()
    }

    fun registerOfBlock(tag: TagKey<Block>, configuration: ModTagConfiguration<Block>.() -> Unit) {
        val configuration = ModTagConfiguration(Registries.BLOCK, tag, configuration)
        configurations.add(configuration)
        tags[tag] = configuration.build()
    }

    fun registerOfEntityType(tag: TagKey<EntityType<*>>, configuration: ModTagConfiguration<EntityType<*>>.() -> Unit) {
        val configuration = ModTagConfiguration(Registries.ENTITY_TYPE, tag, configuration)
        configurations.add(configuration)
        tags[tag] = configuration.build()
    }

    fun registerOfBlockEntityType(tag: TagKey<BlockEntityType<*>>, configuration: ModTagConfiguration<BlockEntityType<*>>.() -> Unit) {
        val configuration = ModTagConfiguration(Registries.BLOCK_ENTITY_TYPE, tag, configuration)
        configurations.add(configuration)
        tags[tag] = configuration.build()
    }

    fun <T : Any> getConfigurations(target: ResourceKey<Registry<T>>): Set<ModTagConfiguration<T>> {
        return configurations.filter { it.target == target }.map { it as ModTagConfiguration<T> }.toSet()
    }

    fun <T : Any> getTag(tag: TagKey<T>): Tag<T> {
        return tags[tag] as Tag<T>? ?: throw IllegalArgumentException("タグ '$tag' がレジストリに見つかりませんでした")
    }
}
