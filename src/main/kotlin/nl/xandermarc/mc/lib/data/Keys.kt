package nl.xandermarc.mc.lib.data

import org.bukkit.NamespacedKey

object Keys {
    private val lazyMap = HashMap<String, NamespacedKey>()
    fun lazy(key: String) = lazyMap.getOrPut(key) { key(key) }
    fun key(key: String) = NamespacedKey(Globals.instance, key)

    val MARKER = key("xmc.marker")

    object Item {
        val UUID = key("xmc.item.uuid")
        val TEMP = key("xmc.item.temporary")
    }
}
