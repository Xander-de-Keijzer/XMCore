package nl.xandermarc.mc.lib.editor.event

import org.bukkit.inventory.ItemStack

class RightClickEvent(
    item: ItemStack,
) : EditorEvent(item)
