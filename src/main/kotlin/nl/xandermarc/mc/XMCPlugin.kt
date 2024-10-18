package nl.xandermarc.mc

import nl.xandermarc.mc.lib.XMC
import nl.xandermarc.mc.ride.RideManager
import nl.xandermarc.mc.ride.tracked.track.TrackCommand
import nl.xandermarc.mc.test.TestTrackedRide
import org.bukkit.plugin.java.JavaPlugin

@Suppress("UnstableApiUsage")
class XMCPlugin : JavaPlugin() {

    init { XMC.init(this) }

    override fun onEnable() {
        TestTrackedRide.enable()
        TrackCommand.register()
        logger.info("${pluginMeta.displayName} has been enabled.")
    }

    override fun onDisable() {
        RideManager.disable()
        XMC.completeJobs()
        logger.info("${pluginMeta.displayName} has been disabled.")
    }

}
