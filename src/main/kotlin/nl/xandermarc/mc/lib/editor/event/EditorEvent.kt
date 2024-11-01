package nl.xandermarc.mc.lib.editor.event

import nl.xandermarc.mc.lib.editor.Editor
import nl.xandermarc.mc.lib.editor.Tool
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
    fun <T: Editor<T>> isToggleTools(tool: Tool<T>): Boolean {
        if (tool != item) return false
        return when(this) {
            is ChatEvent -> tool.onChat is Tool.ToggleEvent
            is LeftClickEvent -> tool.onLeftClick is Tool.ToggleEvent
            is RightClickEvent -> tool.onRightClick is Tool.ToggleEvent
        }
    }
}
