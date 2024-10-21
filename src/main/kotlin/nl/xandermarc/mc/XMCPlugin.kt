package nl.xandermarc.mc

import nl.xandermarc.mc.lib.XMC
import nl.xandermarc.mc.ride.RideManager
import nl.xandermarc.mc.ride.tracked.track.TrackCommand
import nl.xandermarc.mc.test.TestTrackedRide
import org.bukkit.plugin.java.JavaPlugin

@Suppress("UnstableApiUsage")
class XMCPlugin : JavaPlugin() {

    override fun onLoad() {
        XMC.onLoad(this)
    }

    override fun onEnable() {
        XMC.onEnable()
        TestTrackedRide.enable()
        TrackCommand.register()
        logger.info("${pluginMeta.displayName} has been enabled.")
    }

    override fun onDisable() {
        RideManager.disable()
        XMC.onDisable()
        logger.info("${pluginMeta.displayName} has been disabled.")
    }

}
