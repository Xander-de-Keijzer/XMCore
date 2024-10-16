package nl.xandermarc.mc.rides.editor

import nl.xandermarc.mc.lib.pdc.isItem
import nl.xandermarc.mc.lib.pdc.item
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

class Tool<T>(
    private val material: Material,
    val onRightClick: Event<T> = Event(null) {},
    val onLeftClick: Event<T> = Event(null) {},
    val onClick: Event<T> = Event(null) {}
) {
    private val uuid = UUID.randomUUID()
    private val item: ItemStack
        get() = item(material, uuid)

    fun addTo(player: Player, slot: Int) {
        player.inventory.setItem(slot, item)
    }

    fun removeFrom(player: Player) = player.inventory.removeAll { it.isItem(uuid) }

    class Event<T>(val name: String?, private val action: T.() -> Unit) {
        fun invoke(editor: T) = action.invoke(editor)
    }
}
