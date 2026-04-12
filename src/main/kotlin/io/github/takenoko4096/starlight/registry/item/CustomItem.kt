package io.github.takenoko4096.starlight.registry.item

import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level

abstract class CustomItem(properties: Properties, private val eventDispatcher: ItemEventsConfiguration.ItemEventDispatcher) : Item(properties) {
    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
        val event = ItemEventsConfiguration.InteractEvent(level, player, hand)
        eventDispatcher.dispatch(ItemEventsConfiguration.InteractEvent::class, event)
        return InteractionResult.SUCCESS
    }
}
