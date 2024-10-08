package nl.xandermarc.mc.rides.editor

import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent

object EditorManager {

    private val editors: ArrayList<Editor<*>> = arrayListOf()

    fun registerEditor(editor: Editor<*>) {
        editors.add(editor)
    }

    fun isUsingEditor(player: Player): Boolean {
        for (editor in editors) {
            if (editor.player == player) {
                return true
            }
        }
        return false
    }

    fun unregisterEditor(editor: Editor<*>) {
        editors.remove(editor)
    }

    fun stopEditor(player: Player) {
        editors.removeAll { it.apply { stop() }.player == player }
    }

    fun stopEditors() {
        editors.forEach { it.stop() }
        editors.clear()
    }

    fun handleClick(event: PlayerInteractEvent) {
        for (editor in editors) {
            editor.click(event.player, 0)
        }
    }

}