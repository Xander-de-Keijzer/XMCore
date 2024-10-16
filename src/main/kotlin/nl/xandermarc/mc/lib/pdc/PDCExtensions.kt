package nl.xandermarc.mc.lib.pdc

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

fun ItemStack.has(key: NamespacedKey) =
    itemMeta?.persistentDataContainer?.has(key) ?: false

fun <P : Any, C : Any> ItemStack.get(key: NamespacedKey, type: PersistentDataType<P, C>) =
    itemMeta?.persistentDataContainer?.get(key, type)

fun <P : Any, C : Any> ItemStack.set(key: NamespacedKey, type: PersistentDataType<P, C>, value: C) =
    itemMeta?.persistentDataContainer?.set(key, type, value)

fun item(material: Material) = item(material, UUID.randomUUID())

fun item(material: Material, uuid: UUID) =
    ItemStack(material).apply {
        set(ItemKeys.XMC_ITEM, PersistentDataType.BOOLEAN, true)
        set(ItemKeys.XMC_ITEM_UUID_HIGH, PersistentDataType.LONG, uuid.mostSignificantBits)
        set(ItemKeys.XMC_ITEM_UUID_LOW, PersistentDataType.LONG, uuid.leastSignificantBits)
    }

fun ItemStack.hasUUID() = has(ItemKeys.XMC_ITEM_UUID_HIGH) && has(ItemKeys.XMC_ITEM_UUID_LOW)

fun ItemStack.getUUID() = UUID(
    get(ItemKeys.XMC_ITEM_UUID_HIGH, PersistentDataType.LONG)!!,
    get(ItemKeys.XMC_ITEM_UUID_LOW, PersistentDataType.LONG)!!
)

fun ItemStack.isItem() = has(ItemKeys.XMC_ITEM)

fun ItemStack.isItem(uuid: UUID) =
    hasUUID() && getUUID() == uuid
