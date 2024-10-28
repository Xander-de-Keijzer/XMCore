package nl.xandermarc.mc

import nl.xandermarc.mc.core.XMC
import nl.xandermarc.mc.core.XMCProtocol
import nl.xandermarc.mc.lib.extensions.pluginName
import nl.xandermarc.mc.ride.RideManager
import nl.xandermarc.mc.ride.editor.EditorManager
import nl.xandermarc.mc.ride.tracked.track.TrackCommand
import nl.xandermarc.mc.ride.tracked.track.TrackManager
import nl.xandermarc.mc.test.TestTrackedRide
import org.bukkit.plugin.java.JavaPlugin

class XMCPlugin : JavaPlugin() {

    override fun onEnable() {
        logger.info("$pluginName is being enabled...")
        XMC.enable(this)
        XMCProtocol.enable()

        TestTrackedRide.enable()
        TrackCommand.register()
        logger.info("$pluginName has been enabled.")
    }

    override fun onDisable() {
        logger.info("$pluginName is being disabled...")
        RideManager.disable()
        EditorManager.disable()
        TrackManager.disable()

        XMCProtocol.disable()
        XMC.disable()
        logger.info("$pluginName has been disabled.")
    }

}
