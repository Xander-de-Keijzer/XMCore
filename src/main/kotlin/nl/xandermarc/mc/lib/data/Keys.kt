package nl.xandermarc.mc.lib.data

import org.bukkit.NamespacedKey

object Keys {
    private class LazyKey(private val key: String) : Lazy<NamespacedKey> {
        private var namespacedKey: NamespacedKey? = null
        private fun createKey(): NamespacedKey = NamespacedKey(Globals.instance, key).apply { namespacedKey = this }
        override val value: NamespacedKey get() = namespacedKey ?: createKey()
        override fun isInitialized() = namespacedKey != null
    }

    val MARKER by LazyKey("xmc.marker")

    object Item {
        val UUID by LazyKey("xmc.item.uuid")
        val TEMP by LazyKey("xmc.item.temporary")
    }

}
