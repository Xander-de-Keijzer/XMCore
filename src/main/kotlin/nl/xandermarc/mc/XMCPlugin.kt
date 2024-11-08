package nl.xandermarc.mc

import nl.xandermarc.mc.core.commands.TrackCommand
import nl.xandermarc.mc.core.managers.*
import nl.xandermarc.mc.lib.data.Globals
import nl.xandermarc.mc.lib.extensions.completeAll
import nl.xandermarc.mc.lib.extensions.pluginName
import nl.xandermarc.mc.lib.utils.Manager
import nl.xandermarc.test.TestCommand
import nl.xandermarc.test.TestTrackedRide
import org.bukkit.plugin.java.JavaPlugin

class XMCPlugin : JavaPlugin() {

    private val managers = listOf(
        EventManager,
        ProtocolManager,
        TrackManager,
        RideManager,
        EditorManager,
        TestTrackedRide
    )

    private val executors = listOf(
        TestCommand,
        TrackCommand,
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
        Globals.supervisor.completeAll()
        logger.info("$pluginName has been disabled.")
    }

}
