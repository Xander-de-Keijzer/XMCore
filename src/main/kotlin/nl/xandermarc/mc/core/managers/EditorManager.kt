package nl.xandermarc.mc.core.managers

import io.papermc.paper.event.player.AsyncChatEvent
import nl.xandermarc.mc.lib.AbstractEditor
import nl.xandermarc.mc.lib.data.Globals
import nl.xandermarc.mc.lib.extensions.plain
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent

object EditorManager: Listener {
    private val editors = mutableListOf<AbstractEditor<*>>()

    fun register(editor: AbstractEditor<*>) = editors.add(editor)
    fun has(player: Player): Boolean = editors.any { it.player == player }
    fun unregister(editor: AbstractEditor<*>) = editors.remove(editor)

    fun closeAll(player: Player) {
        editors.filter {
            it.player == player
        }.forEach {
            it.close()
        }
        if (editors.any { it.player == player })
            throw IllegalStateException("Editors(${editors.filter { it.player == player }}) belonging to ${player.name} were still present after closing.")
    }
    fun closeAll() {
        editors.forEach { it.close() }
        if (editors.isNotEmpty())
            throw IllegalStateException("Editors($editors) were still present after closing.")
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
        editors.filter { it.player == event.player }.forEach {
            it.call(
                AbstractEditor.ChatEvent(
                    event.player.inventory.itemInMainHand,
                    event.message().plain()
                )
            )
        }
    }

    @EventHandler
    private fun playerQuit(event: PlayerQuitEvent) {
        closeAll(event.player)
    }

}
