package io.github.takenoko4096.starlight.datagen.model

import com.ibm.icu.util.ULocale
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.renderer.item.SelectItemModel
import net.minecraft.client.renderer.item.properties.conditional.Broken
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty
import net.minecraft.client.renderer.item.properties.select.ContextDimension
import net.minecraft.client.renderer.item.properties.select.LocalTime
import net.minecraft.client.renderer.item.properties.select.MainHand
import net.minecraft.world.entity.HumanoidArm
import net.minecraft.world.item.Item
import java.util.Optional
import java.util.TimeZone

class ItemModelVariantsRegistrar internal constructor(
    internal val itemModelGenerators: ItemModelGenerators,
    internal val item: Item
) {
    internal fun register() {
        ItemModelUtils.conditional(Broken())
    }
}
