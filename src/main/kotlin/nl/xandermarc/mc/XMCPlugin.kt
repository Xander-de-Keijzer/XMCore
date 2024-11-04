package nl.xandermarc.mc

import nl.xandermarc.mc.core.EventListener
import nl.xandermarc.mc.lib.data.Globals
import nl.xandermarc.mc.lib.editor.EditorManager
import nl.xandermarc.mc.lib.extensions.pluginName
import nl.xandermarc.mc.lib.utils.Manager
import nl.xandermarc.mc.ride.commands.TrackCommand
import nl.xandermarc.mc.ride.managers.RideManager
import nl.xandermarc.mc.ride.managers.TrackManager
import nl.xandermarc.test.network.TestCommand
import nl.xandermarc.test.network.TestTrackedRide
import org.bukkit.plugin.java.JavaPlugin

class XMCPlugin : JavaPlugin() {

    private val managers = listOf(
        EventListener,
        ProtocolManager,
        TrackManager,
        RideManager,
        EditorManager,
        TestTrackedRide
    )

    private val executors = listOf(
        TestCommand,
        TrackCommand
    )

    override fun onEnable() {
        logger.info("$pluginName is being enabled...")
        Globals.instance = this
        managers.forEach { it.enable(this) }
        executors.forEach { it.register(this) }
        logger.info("$pluginName has been enabled.")
    }

    override fun onDisable() {
        logger.info("$pluginName is being disabled...")
        managers.reversed().forEach(Manager::disable)
        Globals.supervisor.complete()
        logger.info("$pluginName has been disabled.")
    }

}
