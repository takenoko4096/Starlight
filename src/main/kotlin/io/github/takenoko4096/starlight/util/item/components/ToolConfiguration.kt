package io.github.takenoko4096.starlight.util.item.components

import io.github.takenoko4096.starlight.StarlightDSL
import io.github.takenoko4096.starlight.StarlightModInitializer
import net.minecraft.core.HolderSet
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.tags.TagKey
import net.minecraft.world.item.component.Tool
import net.minecraft.world.level.block.Block
import java.util.Optional

@StarlightDSL
class ToolConfiguration(mod: StarlightModInitializer, callback: ToolConfiguration.() -> Unit) : AbstractComponentConfiguration<Tool>(mod, DataComponents.TOOL) {
    private var rules = RulesConfiguration {}

    var defaultMiningSpeed: Float = 1f

    var damagePerBlock: Int = 1

    var canDestroyBlocksInCreative = true

    init {
        callback()
    }

    fun rules(callback: RulesConfiguration.() -> Unit) {
        rules = RulesConfiguration(callback)
    }

    override fun build(): Tool {
        return Tool(
            rules.list,
            defaultMiningSpeed,
            damagePerBlock,
            canDestroyBlocksInCreative
        )
    }

    @StarlightDSL
    class RulesConfiguration(callback: RulesConfiguration.() -> Unit) {
        internal val list = mutableListOf<Tool.Rule>()

        init {
            callback()
        }

        private fun rule(set: HolderSet<Block>, callback: RuleConfiguration.() -> Unit) {
            val c = RuleConfiguration(callback)
            list.add(Tool.Rule(
                set,
                if (c.speed == null) Optional.empty() else Optional.of(c.speed!!),
                if (c.correctForDrops == null) Optional.empty() else Optional.of(c.correctForDrops!!)
            ))
        }

        fun tag(tag: TagKey<Block>, callback: RuleConfiguration.() -> Unit) {
            rule(BuiltInRegistries.BLOCK.getOrThrow(tag), callback)
        }

        fun blocks(vararg blocks: Block, callback: RuleConfiguration.() -> Unit) {
            val registry = BuiltInRegistries.BLOCK

            val holders = blocks.map {
                val key = registry.getResourceKey(it)
                if (!key.isPresent) throw IllegalStateException("Unknown block for tool rule: ${it.name}")
                registry.getOrThrow(key.get())
            }
            val set = HolderSet.direct(*holders.toTypedArray())
            rule(set, callback)
        }

        @StarlightDSL
        class RuleConfiguration(callback: RuleConfiguration.() -> Unit) {
            var speed: Float? = null

            var correctForDrops: Boolean? = null

            init {
                callback()
            }
        }
    }
}
