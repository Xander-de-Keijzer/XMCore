package nl.xandermarc.mc.lib.pdc

import nl.xandermarc.mc.XMCPlugin
import org.bukkit.NamespacedKey

object ItemKeys {
    internal class LazyKey(private val key: String) : Lazy<NamespacedKey> {
        private var namespacedKey: NamespacedKey? = null
        private fun createKey(): NamespacedKey =
            NamespacedKey(XMCPlugin.instance, key).apply {
                namespacedKey = this
                return this
            }

        override val value: NamespacedKey
            get() = namespacedKey ?: createKey()

        override fun isInitialized() = namespacedKey != null
    }

    val XMC_ITEM by LazyKey("xmc.item")
    val XMC_ITEM_UUID_LOW by LazyKey("xmc.item.uuid.low")
    val XMC_ITEM_UUID_HIGH by LazyKey("xmc.item.uuid.high")
}
