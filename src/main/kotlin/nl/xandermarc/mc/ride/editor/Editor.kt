package nl.xandermarc.mc.ride.editor

import nl.xandermarc.mc.lib.extensions.debug
import org.bukkit.entity.Player

abstract class Editor<T : Editor<T>>(val player: Player, vararg toolMaps: Map<Int, Tool<T>>) {
    protected abstract val instance: T
    private val toolMapList = toolMaps.toList()
    private var loadedTools: Int = 0

    private fun loadTools(index: Int) {
        if (loadedTools == index) return
        unloadTools()
        toolMapList[index].forEach { (slot, tool) ->
            tool.addTo(player, slot)
        }
        loadedTools = index
    }

    private fun unloadTools() {
        toolMapList[loadedTools].values.forEach { tool ->
            player.inventory.removeAll { tool == it }
        }
    }

    fun toggleTools() {
        loadTools((loadedTools + 1) % toolMapList.size)
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

    fun close() {
        unloadTools()
        stop()
    }

    protected abstract fun stop()
}
