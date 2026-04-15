package io.github.takenoko4096.starlight.item.components

import com.gmail.takenokoii78.mojangson.MojangsonSerializer
import com.gmail.takenokoii78.mojangson.values.MojangsonCompound
import io.github.takenoko4096.starlight.StarlightModInitializer
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.TagParser
import net.minecraft.world.item.component.CustomData

class CustomDataConfiguration internal constructor(mod: StarlightModInitializer, callback: CustomDataConfiguration.() -> Unit) : AbstractComponentConfiguration<CustomData>(mod, DataComponents.CUSTOM_DATA) {
    val compound = MojangsonCompound()

    init {
        callback()
    }

    override fun build(): CustomData {
        val tag = TagParser.parseCompoundFully(MojangsonSerializer.serialize(compound))
        return CustomData.of(tag)
    }
}
