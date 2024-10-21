package nl.xandermarc.mc

import nl.xandermarc.mc.core.XMCProtocol
import nl.xandermarc.mc.core.XMC
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
        logger.info("${pluginMeta.displayName} is being enabled...")
        XMC.enable()
        XMCProtocol.enable()

        TestTrackedRide.enable()
        TrackCommand.register()
        logger.info("${pluginMeta.displayName} has been enabled.")
    }

    override fun onDisable() {
        logger.info("${pluginMeta.displayName} is being disabled...")
        RideManager.disable()

        XMCProtocol.close()
        XMC.disable()
        logger.info("${pluginMeta.displayName} has been disabled.")
    }

}
