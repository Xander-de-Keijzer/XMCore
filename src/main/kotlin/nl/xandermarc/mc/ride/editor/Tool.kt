package nl.xandermarc.mc.ride.editor

import nl.xandermarc.mc.lib.extensions.compare
import nl.xandermarc.mc.lib.extensions.temp
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

data class Tool<T : Editor<T>>(
    private val material: Material,
    val onRightClick: Event<T, Unit> = EmptyEvent(),
    val onLeftClick: Event<T, Unit> = EmptyEvent(),
    val onChat: Event<T, String> = EmptyEvent(),
) {
    private val item: ItemStack = temp(material)

    fun addTo(player: Player, slot: Int) {
        player.inventory.setItem(slot, item)
    }

    sealed class Event<T, U>(val name: String? = null, private val action: T.(U) -> Unit = {}) {
        operator fun invoke(editor: T, context: U) = action.invoke(editor, context)
        override operator fun equals(other: Any?): Boolean {
            return when(this) {
                is EmptyEvent -> other is EmptyEvent<*, *>
                is ToggleEvent -> other is ToggleEvent<*>
                is ToolEvent -> { other is ToolEvent<*, *> && this === other }
            }
        }
        override fun hashCode(): Int {
            var result = name?.hashCode() ?: 0
            result = 31 * result + action.hashCode()
            return result
        }
    }
    class ToolEvent<T, U>(name: String, action: T.(U) -> Unit) : Event<T, U>(name, action)
    class EmptyEvent<T, U> : Event<T, U>()
    class ToggleEvent<T : Editor<T>> : Event<T, Unit>("Toggle tools", {toggleTools()})

    override fun equals(other: Any?) = item.compare(other)
    override fun hashCode(): Int {
        var result = material.hashCode()
        result = 31 * result + item.hashCode()
        return result
    }
}
