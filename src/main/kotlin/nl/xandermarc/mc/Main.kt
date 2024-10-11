package nl.xandermarc.mc

import nl.xandermarc.mc.lib.commands.ExampleCommand
import nl.xandermarc.mc.lib.commands.registerCommands
import nl.xandermarc.mc.lib.logging.info
import nl.xandermarc.mc.rides.RideManager
import nl.xandermarc.mc.rides.objects.TestTrackedRide
import nl.xandermarc.mc.rides.tracked.TrackManager
import org.bukkit.World
import org.bukkit.plugin.java.JavaPlugin

@Suppress("UnstableApiUsage")
class Main: JavaPlugin() {

    companion object {
        lateinit var instance: JavaPlugin
        val world: World by lazy { instance.server.worlds.first() }
    }

    override fun onEnable() {
        instance = this

        RideManager.register(
            TestTrackedRide
        )
        info("Done registering")

        registerCommands(
            ExampleCommand,
            TrackManager
        )

        logger.info("${pluginMeta.displayName} has been Enabled")
    }

}
