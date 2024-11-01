package nl.xandermarc.mc.lib.editor

import io.papermc.paper.event.player.AsyncChatEvent
import nl.xandermarc.mc.lib.data.Globals
import nl.xandermarc.mc.lib.editor.event.ChatEvent
import nl.xandermarc.mc.lib.extensions.plain
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent

object EditorManager: Listener {

    private val editors = mutableListOf<Editor<*>>()
    private fun editor(player: Player, f: Editor<*>.() -> Unit) {
        editors.filter { it.player == player }.forEach { f(it) }
    }

    fun registerEditor(editor: Editor<*>) {
        editors.add(editor)
    }

    fun hasEditor(player: Player): Boolean =
        editors.any { it.player == player }

    fun closeEditor(player: Player) {
        editor(player) {
            close()
            editors.remove(this)
        }
    }

    fun disable() {
        editors.forEach { it.close() }
        editors.clear()
    }

    @EventHandler
    private fun clickEvent(event: PlayerInteractEvent) {
        Globals.logger.info("$event")
    }

    @EventHandler
    private fun clickEvent(event: PlayerInteractEntityEvent) {
        Globals.logger.info("$event")
    }

    @EventHandler
    private fun clickEvent(event: PlayerInteractAtEntityEvent) {
        Globals.logger.info("$event")
    }

    @EventHandler
    private fun chatEvent(event: AsyncChatEvent) {
        editor(event.player) {
            call(
                ChatEvent(
                event.player.inventory.itemInMainHand,
                event.message().plain()
            )
            )
        }
    }

    @EventHandler
    private fun playerQuit(event: PlayerQuitEvent) {
        closeEditor(event.player)
    }

}