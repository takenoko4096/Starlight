package io.github.takenoko4096.starlight.registry.creativetab

import io.github.takenoko4096.starlight.StarlightModInitializer
import io.github.takenoko4096.starlight.registry.StarlightRegistry
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.CreativeModeTab

class ModCreativeModeTabRegistry internal constructor(mod: StarlightModInitializer) : StarlightRegistry(mod) {
    private val configurations = mutableSetOf<ModCreativeModeTabConfiguration>()

    private val tabs = mutableMapOf<ResourceKey<CreativeModeTab>, CreativeModeTab>()

    fun register(name: String, configuration: ModCreativeModeTabConfiguration.() -> Unit): CreativeModeTab {
        val c = ModCreativeModeTabConfiguration(mod, name, configuration)
        configurations.add(c)
        val t = c.register()
        tabs[c.resourceKey] = t
        return t
    }

    fun getConfigurations(): Set<ModCreativeModeTabConfiguration> {
        return configurations.toSet()
    }

    fun getCreativeModeTab(key: ResourceKey<CreativeModeTab>): CreativeModeTab {
        return tabs[key] ?: throw IllegalArgumentException("タブ '$key' がレジストリに見つかりませんでした")
    }
}
