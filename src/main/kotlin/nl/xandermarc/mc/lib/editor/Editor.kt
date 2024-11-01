package nl.xandermarc.mc.lib.editor

import nl.xandermarc.mc.lib.editor.event.EditorEvent
import nl.xandermarc.mc.lib.extensions.warn
import org.bukkit.entity.Player

abstract class Editor<T : Editor<T>>(val player: Player, vararg toolMaps: Map<Int, Tool<T>>) {
    protected abstract val instance: T
    private val toolMapList = toolMaps.toList()
    private var toolMapIterator = toolMapList.iterator()

    private fun unloadTools() {
        toolMapList.flatMap { it.values }.forEach { tool ->
            player.inventory.removeAll { tool == it }
        }
    }

    private fun toggleTools() {
        unloadTools()
        if (!toolMapIterator.hasNext()) toolMapIterator = toolMapList.iterator()
        toolMapIterator.next().forEach { (slot, tool) ->
            player.inventory.setItem(slot, tool.item)
        }
    }

    fun call(event: EditorEvent) {
        toolMapList
            .flatMap { it.values }
            .filter { it.item == event.item }
            .forEach { tool ->
                if (event.isToggleTools(tool)) toggleTools()
                else event(instance, tool)
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
