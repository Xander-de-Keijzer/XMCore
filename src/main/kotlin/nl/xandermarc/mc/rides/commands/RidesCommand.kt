package nl.xandermarc.mc.rides.commands

import com.mojang.brigadier.Command
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import nl.xandermarc.mc.rides.editor.EditorManager
import nl.xandermarc.mc.rides.tracked.TrackEditor
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

@Suppress("UnstableApiUsage")
fun Plugin.rideCommand() {
    lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
        event.registrar().register(
            Commands.literal("ride")
                .executes { ctx ->
                    (ctx.source.sender as? Player)?.let { player ->
                        if (EditorManager.isUsingEditor(player)) {
                            EditorManager.stopEditor(player)
                            player.sendMessage("Stopped editing")
                        } else {
                            EditorManager.registerEditor(TrackEditor("test", player))
                            player.sendMessage("Started editing")
                        }
                    }
                    Command.SINGLE_SUCCESS
                }
                .build(),
            "some bukkit help description string",
            listOf()
        )
    }
}