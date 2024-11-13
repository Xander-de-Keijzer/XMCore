package nl.xandermarc.mc.lib.data

import org.bukkit.Location
import org.bukkit.configuration.file.FileConfiguration

object Config {
    val instance get() = Globals.instance.config
    private class LazyEntry<T>(private val builder: FileConfiguration.() -> T?) : Lazy<T> {
        private var entry: T? = null
        private fun buildEntry(): T = builder(instance)!!.apply { entry = this }
        override val value: T get() = entry ?: buildEntry()
        override fun isInitialized() = entry != null
    }

    object Messages {
        val JOIN by LazyEntry { getString("messages.join") }
        val QUIT by LazyEntry { getString("messages.quit") }
    }

    object Defaults {
        val WORLD by LazyEntry { getString("defaults.world") }
        val LOCATION by LazyEntry {
            Location(
                Globals.world,
                getDouble("defaults.location.x"),
                getDouble("defaults.location.y"),
                getDouble("defaults.location.z"),
                getDouble("defaults.location.yaw").toFloat(),
                getDouble("defaults.location.pitch").toFloat()
            )
        }
    }
}
