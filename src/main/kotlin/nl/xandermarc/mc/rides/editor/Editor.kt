package nl.xandermarc.mc.rides.editor

import nl.xandermarc.mc.lib.extensions.log
import org.bukkit.entity.Player

abstract class Editor<T: Editor<T>>(val player: Player, vararg toolMaps: Map<Int, Tool<T>>) {
    protected abstract val instance: T
    private val toolMapList = toolMaps.toList()
    private var loadedTools: Int = 0

    fun toggleTools() {
        loadedTools = (loadedTools + 1) % toolMapList.size
    }

    fun click(player: Player, clicked: Int) {

        log { "${player.name} clicked at ${clicked}" }
        if (player != this.player) return

        toolMapList.forEach { toolMap ->
            toolMap.forEach { (slot, tool) ->
                if (clicked == slot) {
                    log { "Clicked at ${player.name} to $slot" }
                    tool.onRightClick?.second?.invoke(instance)
                    tool.onClick?.second?.invoke(instance)
                }
            }
        }
    }

    abstract fun stop()
}
