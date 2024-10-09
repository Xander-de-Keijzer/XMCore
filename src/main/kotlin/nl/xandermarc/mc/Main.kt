package nl.xandermarc.mc

import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.java.JavaPlugin


class Main: JavaPlugin(), Listener {

    companion object {
        lateinit var instance: JavaPlugin
        val world: World
            get() = instance.server.worlds.first()
    }

    override fun onEnable() {
        instance = this

        server.pluginManager.registerEvents(this, this)

        logger.info("Hello world!")
    }

    @EventHandler
    fun onClick(event: PlayerInteractEvent) {
    }

}