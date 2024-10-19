package nl.xandermarc.mc.lib.extensions

import nl.xandermarc.mc.lib.data.Keys
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

fun <P: Any, C: Any> ItemStack.set(key: NamespacedKey, type: PersistentDataType<P, C>, value: C) =
    itemMeta?.persistentDataContainer?.set(key, type, value)

fun ItemStack.has(key: NamespacedKey) =
    itemMeta?.persistentDataContainer?.has(key) ?: false

fun <P: Any, C: Any> ItemStack.get(key: NamespacedKey, type: PersistentDataType<P, C>) =
    itemMeta?.persistentDataContainer?.get(key, type)

fun item(material: Material) = item(material, UUID.randomUUID())
fun item(material: Material, uuid: UUID) =
    ItemStack(material).apply {
        set(Keys.Item.UUID_HIGH, PersistentDataType.LONG, uuid.mostSignificantBits)
        set(Keys.Item.UUID_LOW, PersistentDataType.LONG, uuid.leastSignificantBits)
    }

fun ItemStack.isItem() = has(Keys.Item.UUID_HIGH) && has(Keys.Item.UUID_LOW)
fun ItemStack.isItem(uuid: UUID) = isItem() && getUUID() == uuid

fun ItemStack.getUUID() = UUID(
    get(Keys.Item.UUID_HIGH, PersistentDataType.LONG)!!,
    get(Keys.Item.UUID_LOW, PersistentDataType.LONG)!!
)

fun ItemStack.compare(other: Any?): Boolean {
    if (this === other) return true
    if (other !is ItemStack) return false
    return isItem() && other.isItem() && getUUID() == other.getUUID()
}
