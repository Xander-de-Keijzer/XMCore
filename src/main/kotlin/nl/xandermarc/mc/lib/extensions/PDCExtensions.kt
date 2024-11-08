package nl.xandermarc.mc.lib.extensions

import nl.xandermarc.mc.lib.data.Keys
import nl.xandermarc.mc.lib.serializers.UUIDDataType
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import java.util.*

/*
    PersistentDataContainer
 */

fun PersistentDataContainer.flag(key: NamespacedKey) = apply { set(key, PersistentDataType.BYTE, 1) }
fun PersistentDataContainer.isMarked() = has(Keys.MARKER)
fun PersistentDataContainer.mark() = apply { flag(Keys.MARKER) }
fun PersistentDataContainer.unmark() = apply { remove(Keys.MARKER) }

/*
    ItemMeta
 */

fun <P: Any, C: Any> ItemMeta.set(key: NamespacedKey, type: PersistentDataType<P, C>, value: C) = apply {
    persistentDataContainer.set(key, type, value)
}
fun ItemMeta.has(key: NamespacedKey) =
    persistentDataContainer.has(key)
fun <P: Any, C: Any> ItemMeta.get(key: NamespacedKey, type: PersistentDataType<P, C>): C? =
    persistentDataContainer.get(key, type)

fun ItemMeta.flag(key: NamespacedKey) = apply { persistentDataContainer.flag(key) }
fun ItemMeta.isMarked() = persistentDataContainer.isMarked()
fun ItemMeta.mark() = apply { persistentDataContainer.mark() }
fun ItemMeta.unmark() = apply { persistentDataContainer.unmark() }

/*
    ItemStack
 */

fun <P: Any, C: Any> ItemStack.set(key: NamespacedKey, type: PersistentDataType<P, C>, value: C) = apply {
    itemMeta.set(key, type, value)
}
fun ItemStack.has(key: NamespacedKey) = itemMeta.has(key)
fun <P: Any, C: Any> ItemStack.get(key: NamespacedKey, type: PersistentDataType<P, C>): C? = itemMeta.get(key, type)

fun ItemStack.flag(key: NamespacedKey) = apply { itemMeta.flag(key) }
fun ItemStack.isMarked() = itemMeta.isMarked()
fun ItemStack.mark() = apply { itemMeta.mark() }
fun ItemStack.unmark() = apply { itemMeta.unmark() }

/*
    Utils
 */

fun item(material: Material) = ItemStack(material).mark()
fun item(material: Material, uuid: UUID) = item(material).set(Keys.Item.UUID, UUIDDataType, uuid)

fun temp(material: Material) = item(material).flag(Keys.Item.TEMP)
fun temp(material: Material, uuid: UUID) = item(material, uuid).flag(Keys.Item.TEMP)

fun ItemStack.hasUUID() = has(Keys.Item.UUID)
fun ItemStack.getUUID() = get(Keys.Item.UUID, UUIDDataType)
fun ItemStack.hasUUid(uuid: UUID) = hasUUID() && getUUID() == uuid

fun ItemStack.compare(other: Any?): Boolean {
    if (this === other) return true
    if (other !is ItemStack) return false
    if (!hasUUID() || !other.hasUUID()) return false
    return getUUID() == other.getUUID()
}
