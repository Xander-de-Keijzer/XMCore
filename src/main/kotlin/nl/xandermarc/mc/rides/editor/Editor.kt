package nl.xandermarc.mc.rides.editor

import nl.xandermarc.mc.lib.logging.debug
import org.bukkit.entity.Player

abstract class Editor<T: Editor<T>>(val player: Player, vararg toolMaps: Map<Int, Tool<T>>) {
    protected abstract val instance: T
    private val toolMapList = toolMaps.toList()
    private var loadedTools: Int = 0

    fun toggleTools() {
        loadedTools = (loadedTools + 1) % toolMapList.size
    }

    fun click(clicked: Int) {

        debug { "${player.name} clicked at $clicked" }

        toolMapList.forEach { toolMap ->
            toolMap.filter {
                it.key == clicked
            }.values.forEach { tool ->
                debug { "Clicked at ${player.name} to $clicked" }
                tool.onRightClick.invoke(instance)
                tool.onClick.invoke(instance)
            }
        }
    }

    abstract fun stop()
}
