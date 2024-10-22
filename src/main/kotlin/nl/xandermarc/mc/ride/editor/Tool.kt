package nl.xandermarc.mc.ride.editor

import nl.xandermarc.mc.lib.extensions.compare
import nl.xandermarc.mc.lib.extensions.item
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

data class Tool<T>(
    private val material: Material,
    val onRightClick: Event<T> = Event(null) {},
    val onLeftClick: Event<T> = Event(null) {},
    val onClick: Event<T> = Event(null) {}
) {
    private val item: ItemStack = item(material)

    fun addTo(player: Player, slot: Int) {
        player.inventory.setItem(slot, item)
    }

    class Event<T>(val name: String?, private val action: T.() -> Unit) {
        fun invoke(editor: T) = action.invoke(editor)
    }

    override fun equals(other: Any?) = item.compare(other)
    override fun hashCode(): Int {
        var result = material.hashCode()
        result = 31 * result + item.hashCode()
        return result
    }
}
