package nl.xandermarc.mc.lib

import nl.xandermarc.mc.core.managers.EditorManager
import nl.xandermarc.mc.lib.extensions.getUUID
import nl.xandermarc.mc.lib.extensions.hasUUID
import nl.xandermarc.mc.lib.extensions.temp
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

abstract class AbstractEditor<T>(val instance: T, val player: Player, vararg toolMaps: Map<Int, Tool<T>>) {
    private val toolMapList = toolMaps.toList()
    private var toolMapIterator = toolMapList.iterator()

    private fun unloadTools() {
        toolMapList.flatMap { it.values }.forEach { tool ->
            player.inventory.removeAll { tool.isItem(it) }
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
            .flatMap { toolMap -> toolMap.values }
            .filter { tool -> tool.isItem(event.item) }
            .forEach { tool ->
                when(event) {
                    is ChatEvent -> tool.onChat.second(instance, event.message)
                    is RightClickEvent -> tool.onRightClick.second(instance)
                    is LeftClickEvent -> tool.onLeftClick.second(instance)
                }
            }
    }

    fun open(): Boolean {
        if (EditorManager.has(player)) EditorManager.closeAll(player)
        EditorManager.register(this)
        toggleTools()
        return true
    }

    fun close() {
        unloadTools()
        stop()
        if (!EditorManager.has(player)) throw IllegalStateException("Closed unregistered editor")
        EditorManager.unregister(this)
    }

    protected open fun stop() {}

    data class Tool<T>(
        private val name: String,
        private val material: Material,
        val onRightClick: Pair<String, T.() -> Unit> = "" to {},
        val onLeftClick: Pair<String, T.() -> Unit> = "" to {},
        val onChat: Pair<String, T.(String) -> Unit> = "" to {},
    ) {
        val uuid: UUID = UUID.randomUUID()
        val item: ItemStack = temp(material, uuid)
        fun isItem(item: ItemStack): Boolean {
            if (item === this.item) return true
            if (!item.hasUUID()) return false
            return item.getUUID() == uuid
        }
    }

    sealed class EditorEvent(val item: ItemStack)
    class ChatEvent(
        item: ItemStack,
        val message: String
    ) : EditorEvent(item)

    class LeftClickEvent(
        item: ItemStack,
    ) : EditorEvent(item)

    class RightClickEvent(
        item: ItemStack,
    ) : EditorEvent(item)
}
