package nl.xandermarc.mc.ride.editor

import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent

object EditorManager {

    private val editors = mutableListOf<Editor<*>>()

    fun registerEditor(editor: Editor<*>) {
        editors.add(editor)
    }

    fun isUsingEditor(player: Player): Boolean =
        editors.any { it.player == player }

    fun unregisterEditor(editor: Editor<*>) {
        editors.remove(editor)
    }

    fun stopEditor(player: Player) {
        editors.filter { it.player == player }.forEach { it.close() }
        editors.removeAll { it.player == player }
    }

    fun stopEditors() {
        editors.forEach { it.close() }
        editors.clear()
    }

    fun handleClick(event: PlayerInteractEvent) {
        editors.filter {
            it.player == event.player
        }.forEach {
            it.click(1)
        }
    }

}