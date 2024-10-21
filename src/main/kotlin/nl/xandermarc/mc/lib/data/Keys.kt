package nl.xandermarc.mc.lib.data

import nl.xandermarc.mc.core.XMC
import org.bukkit.NamespacedKey

object Keys {
    internal class LazyKey(private val key: String) : Lazy<NamespacedKey> {
        private var namespacedKey: NamespacedKey? = null
        private fun createKey(): NamespacedKey = NamespacedKey(XMC.instance, key).apply { namespacedKey = this }
        override val value: NamespacedKey get() = namespacedKey ?: createKey()
        override fun isInitialized() = namespacedKey != null
    }

    object Item {
        val UUID_LOW by LazyKey("xmc.item.uuid.low")
        val UUID_HIGH by LazyKey("xmc.item.uuid.high")
    }

}
