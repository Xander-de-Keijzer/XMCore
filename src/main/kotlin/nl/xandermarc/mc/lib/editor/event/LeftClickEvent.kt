package nl.xandermarc.mc.lib.editor.event

import org.bukkit.inventory.ItemStack

class LeftClickEvent(
    item: ItemStack,
) : EditorEvent(item)
