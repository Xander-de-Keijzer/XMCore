package nl.xandermarc.mc.ride.editor.event

import org.bukkit.inventory.ItemStack

class LeftClickEvent(
    item: ItemStack,
) : EditorEvent(item)