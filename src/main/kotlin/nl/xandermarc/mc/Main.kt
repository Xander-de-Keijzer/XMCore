package nl.xandermarc.mc

import nl.xandermarc.mc.lib.commands.ExampleCommand
import nl.xandermarc.mc.lib.commands.commandRegistrar
import nl.xandermarc.mc.lib.commands.registerCommands
import nl.xandermarc.mc.lib.logging.debug
import nl.xandermarc.mc.lib.math.Vec
import nl.xandermarc.mc.rides.editor.Editor
import nl.xandermarc.mc.rides.editor.Tool
import nl.xandermarc.mc.rides.entities.DisplayEntity
import org.bukkit.World
import org.bukkit.entity.Player
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

    }

    override fun onEnable() {
        logger.info("${pluginMeta.displayName} has been Enabled")
    }

}
