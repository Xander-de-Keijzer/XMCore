package nl.xandermarc.mc.lib.extensions

import nl.xandermarc.mc.lib.data.Keys
import nl.xandermarc.mc.lib.serializers.UUIDDataType
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

fun <P: Any, C: Any> ItemStack.set(key: NamespacedKey, type: PersistentDataType<P, C>, value: C) =
    itemMeta?.persistentDataContainer?.set(key, type, value)

fun ItemStack.set(key: NamespacedKey) = set(key, PersistentDataType.BYTE, 1)

fun ItemStack.has(key: NamespacedKey) =
    itemMeta?.persistentDataContainer?.has(key) ?: false

fun <P: Any, C: Any> ItemStack.get(key: NamespacedKey, type: PersistentDataType<P, C>) =
    itemMeta?.persistentDataContainer?.get(key, type)

fun item(material: Material) =
    ItemStack(material).apply {
        set(Keys.Item.XMC)
    }

fun temp(material: Material) =
    item(material).apply {
        set(Keys.Item.TEMP)
    }

fun item(material: Material, uuid: UUID) =
    item(material).apply {
        set(Keys.Item.UUID, UUIDDataType, uuid)
    }

fun temp(material: Material, uuid: UUID) =
    item(material, uuid).apply {
        set(Keys.Item.TEMP)
    }

fun ItemStack.isItem() = has(Keys.Item.XMC)
fun ItemStack.hasUUID() = has(Keys.Item.UUID)
fun ItemStack.getUUID() = get(Keys.Item.UUID, UUIDDataType)
fun ItemStack.isItem(uuid: UUID) = hasUUID() && getUUID() == uuid

fun ItemStack.compare(other: Any?): Boolean {
    if (this === other) return true
    if (other !is ItemStack) return false
    if (!hasUUID() || !other.hasUUID()) return false
    return getUUID() == other.getUUID()
}
