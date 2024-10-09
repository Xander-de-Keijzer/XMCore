package nl.xandermarc.mc

import nl.xandermarc.mc.lib.commands.ExampleCommand
import nl.xandermarc.mc.lib.commands.commandRegistrar
import nl.xandermarc.mc.lib.commands.registerCommands
import org.bukkit.World
import org.bukkit.plugin.java.JavaPlugin

@Suppress("UnstableApiUsage")
class Main: JavaPlugin() {

    companion object {
        lateinit var instance: JavaPlugin
        val world: World by lazy { instance.server.worlds.first() }
    }

    override fun onLoad() {
        instance = this

        registerCommands(
            ExampleCommand
        )

        commandRegistrar {
            server.dispatchCommand(server.consoleSender, "example")
            server.dispatchCommand(server.consoleSender, "example show")
            server.dispatchCommand(server.consoleSender, "example list test")
            server.dispatchCommand(server.consoleSender, "example list test2")
            server.dispatchCommand(server.consoleSender, "example bs")
        }
    }

    override fun onEnable() {
        logger.info("${pluginMeta.displayName} has been Enabled")
    }

}