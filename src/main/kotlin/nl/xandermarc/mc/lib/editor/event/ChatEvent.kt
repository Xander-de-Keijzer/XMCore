package nl.xandermarc.mc.lib.editor.event

import org.bukkit.inventory.ItemStack

class ChatEvent(
    item: ItemStack,
    val message: String
) : EditorEvent(item)
