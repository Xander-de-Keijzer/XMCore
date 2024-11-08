package nl.xandermarc.mc.lib.editor

import nl.xandermarc.mc.lib.extensions.compare
import nl.xandermarc.mc.lib.extensions.temp
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.*

data class Tool<T : Editor<T>>(
    private val material: Material,
    val onRightClick: Event<T, Unit> = EmptyEvent(),
    val onLeftClick: Event<T, Unit> = EmptyEvent(),
    val onChat: Event<T, String> = EmptyEvent(),
) {
    val uuid: UUID = UUID.randomUUID()
    val item: ItemStack = temp(material, uuid)

    sealed class Event<T, U>(val name: String? = null, private val action: T.(U) -> Unit = {}) {
        operator fun invoke(editor: T, context: U) = action.invoke(editor, context)
    }
    class ToolEvent<T, U>(name: String, action: T.(U) -> Unit) : Event<T, U>(name, action)
    class EmptyEvent<T, U> : Event<T, U>()
    class ToggleEvent<T : Editor<T>> : Event<T, Unit>("Toggle tools")

    override fun equals(other: Any?) = item.compare(other)
    override fun hashCode(): Int {
        var result = material.hashCode()
        result = 31 * result + item.hashCode()
        return result
    }
}
