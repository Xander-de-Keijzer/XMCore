@file:JvmName("Extensions")
@file:JvmMultifileClass
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

fun PersistentDataContainer.hasMark() = has(Keys.MARKER)

fun PersistentDataContainer.getInt(key: NamespacedKey): Int? = get(key, PersistentDataType.INTEGER)
fun PersistentDataContainer.getStr(key: NamespacedKey): String? = get(key, PersistentDataType.STRING)
fun PersistentDataContainer.getDbl(key: NamespacedKey): Double? = get(key, PersistentDataType.DOUBLE)

fun PersistentDataContainer.flag(key: NamespacedKey) = apply { set(key, PersistentDataType.BYTE, 1) }
fun PersistentDataContainer.mark() = apply { flag(Keys.MARKER) }
fun PersistentDataContainer.unmark() = apply { remove(Keys.MARKER) }

fun PersistentDataContainer.setInt(key: NamespacedKey, value: Int) = apply { set(key, PersistentDataType.INTEGER, value) }
fun PersistentDataContainer.setStr(key: NamespacedKey, value: String) = apply { set(key, PersistentDataType.STRING, value) }
fun PersistentDataContainer.setDbl(key: NamespacedKey, value: Double) = apply { set(key, PersistentDataType.DOUBLE, value) }

/*
    ItemMeta
 */


fun ItemMeta.has(key: NamespacedKey) = persistentDataContainer.has(key)
fun ItemMeta.hasMark() = persistentDataContainer.hasMark()
fun <P: Any, C: Any> ItemMeta.get(key: NamespacedKey, type: PersistentDataType<P, C>): C? = persistentDataContainer.get(key, type)

fun <P: Any, C: Any> ItemMeta.set(key: NamespacedKey, type: PersistentDataType<P, C>, value: C) = apply {
    persistentDataContainer.set(key, type, value)
}
fun ItemMeta.flag(key: NamespacedKey) = apply { persistentDataContainer.flag(key) }
fun ItemMeta.mark() = apply { persistentDataContainer.mark() }
fun ItemMeta.unmark() = apply { persistentDataContainer.unmark() }

/*
    ItemStack
 */


fun ItemStack.has(key: NamespacedKey) = itemMeta.has(key)
fun ItemStack.hasMark() = itemMeta.hasMark()
fun <P: Any, C: Any> ItemStack.get(key: NamespacedKey, type: PersistentDataType<P, C>): C? = itemMeta.get(key, type)

fun <P: Any, C: Any> ItemStack.set(key: NamespacedKey, type: PersistentDataType<P, C>, value: C) = apply {
    editMeta { it.set(key, type, value) }
}
fun ItemStack.flag(key: NamespacedKey) = apply { editMeta { it.flag(key) } }
fun ItemStack.mark() = apply { editMeta { it.mark() } }
fun ItemStack.unmark() = apply { editMeta { it.unmark() } }

/*
    Utils
 */

fun item(material: Material) = ItemStack.of(material).mark()
fun item(material: Material, uuid: UUID) = item(material).set(Keys.Item.UUID, UUIDDataType, uuid)

fun temp(material: Material) = item(material).flag(Keys.Item.TEMP)
fun temp(material: Material, uuid: UUID) = item(material, uuid).flag(Keys.Item.TEMP)

fun ItemStack.hasUUID() = has(Keys.Item.UUID)
fun ItemStack.getUUID() = get(Keys.Item.UUID, UUIDDataType)
fun ItemStack.hasUUID(uuid: UUID) = hasUUID() && getUUID() == uuid
