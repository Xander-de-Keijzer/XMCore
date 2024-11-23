package nl.xandermarc.mc

import nl.xandermarc.mc.core.commands.TrackCommand
import nl.xandermarc.mc.core.managers.AsyncManager
import nl.xandermarc.mc.core.managers.EditorManager
import nl.xandermarc.mc.core.managers.EventManager
import nl.xandermarc.mc.core.sql.createTables
import nl.xandermarc.mc.core.sql.tables.SessionCoercions
import nl.xandermarc.mc.core.sql.tables.UserSessions
import nl.xandermarc.mc.core.sql.tables.Users
import nl.xandermarc.mc.lib.data.Globals.instance
import nl.xandermarc.mc.lib.data.Globals.supervisor
import nl.xandermarc.mc.lib.extensions.*
import nl.xandermarc.test.TestCommand
import nl.xandermarc.test.TestTrackedRide
import org.bukkit.plugin.java.JavaPlugin

class XMCPlugin : JavaPlugin() {

    override fun onLoad() {
        val millis = measureMillis { time ->
            reloadConfig().complete(time) { "Config reloaded. ($it)" }
            saveConfig().complete(time) { "Config saved. ($it)" }
            createTables(
                Users,
                UserSessions,
                SessionCoercions
            ).complete(time) { "Tables created. ($it)" }
            instance = this
        }
        logger.info("$pluginName has been loaded. ($millis ms)")
    }

    override fun onEnable() {
        val millis = measureMillis { time ->
            AsyncManager.enable(this).enabled(time)
            EventManager.enable(this).enabled(time)
            XMCProtocol.enable(this).enabled(time)
            TestTrackedRide.enable().enabled(time)

            TestCommand.register(this).registered(time)
            TrackCommand.register(this).registered(time)
        }
        logger.info("$pluginName has been enabled. ($millis ms)")
    }

    override fun onDisable() {
        val millis = measureMillis { time ->
            TestTrackedRide.disable().disabled(time)

            EditorManager.closeAll().disabled(time)
            XMCProtocol.disable().disabled(time)
            EventManager.disable().disabled(time)
            AsyncManager.disable().disabled(time)
            supervisor.completeAll().complete(time) { "Coroutines completed. ($it)" }
        }
        logger.info("$pluginName has been disabled. ($millis ms)")
    }

}
