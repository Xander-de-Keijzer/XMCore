package nl.xandermarc.mc.ride.editor.event

import nl.xandermarc.mc.ride.editor.Editor
import nl.xandermarc.mc.ride.editor.Tool
import org.bukkit.inventory.ItemStack

sealed class EditorEvent(val item: ItemStack) {
    operator fun <T: Editor<T>> invoke(editor: T, tool: Tool<T>) {
        if (tool != item) return
        when(this) {
            is ChatEvent -> tool.onChat(editor, message)
            is LeftClickEvent -> tool.onLeftClick(editor, Unit)
            is RightClickEvent -> tool.onRightClick(editor, Unit)
        }
    }
}
