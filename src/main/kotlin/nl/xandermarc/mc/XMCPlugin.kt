package nl.xandermarc.mc

import nl.xandermarc.mc.lib.data.Globals
import nl.xandermarc.mc.lib.editor.EditorManager
import nl.xandermarc.mc.lib.extensions.pluginName
import nl.xandermarc.mc.ride.managers.RideManager
import nl.xandermarc.mc.ride.commands.TrackCommand
import nl.xandermarc.mc.ride.managers.TrackManager
import nl.xandermarc.mc.test.TestCommand
import nl.xandermarc.mc.test.TestTrackedRide
import org.bukkit.plugin.java.JavaPlugin

class XMCPlugin : JavaPlugin() {

    override fun onEnable() {
        logger.info("$pluginName is being enabled...")
        Globals.instance = this
        XMCProtocol.enable()

        TestCommand.register()
        TestTrackedRide.enable()
        TrackCommand.register()
        logger.info("$pluginName has been enabled.")
        server.pluginManager.disablePlugin(this)
    }

    override fun onDisable() {
        logger.info("$pluginName is being disabled...")
        RideManager.disable()
        EditorManager.disable()
        TrackManager.disable()

        XMCProtocol.disable()
        Globals.supervisor.complete()
        logger.info("$pluginName has been disabled.")
    }

}
