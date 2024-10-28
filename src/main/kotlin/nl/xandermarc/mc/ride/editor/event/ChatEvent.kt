package nl.xandermarc.mc.ride.editor.event

import org.bukkit.inventory.ItemStack

class ChatEvent(
    item: ItemStack,
    val message: String
) : EditorEvent(item)
