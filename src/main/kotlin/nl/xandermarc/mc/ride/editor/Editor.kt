package nl.xandermarc.mc.ride.editor

import nl.xandermarc.mc.lib.extensions.warn
import nl.xandermarc.mc.ride.editor.event.EditorEvent
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
        toolMapList.flatMap { it.values }.forEach { tool ->
            player.inventory.removeAll { tool == it }
        }
    }

    fun toggleTools() {
        loadTools((loadedTools + 1) % toolMapList.size)
    }

    fun call(event: EditorEvent) {
        toolMapList.flatMap { it.values }.forEach { tool ->
            event(instance, tool)
        }
    }

    fun close() {
        unloadTools()
        stop()
    }

    protected abstract fun stop()

    // Util can be removed
    init {
        if (toolMaps.size > 1) {
            if (!toolMapList.all { toolMap ->
                toolMap.values.any { tool ->
                    tool.onChat is Tool.ToggleEvent ||
                    tool.onRightClick is Tool.ToggleEvent ||
                    tool.onLeftClick is Tool.ToggleEvent
                }
            }) {
                warn("Editor $this for player ${player.name} was created with multiple tool maps, " +
                        "but not every tool map has a toggle tool to switch between them.")
            }
        }
    }
}
