package nl.xandermarc.mc.ride.editor.event

import org.bukkit.inventory.ItemStack

class RightClickEvent(
    item: ItemStack,
) : EditorEvent(item)
