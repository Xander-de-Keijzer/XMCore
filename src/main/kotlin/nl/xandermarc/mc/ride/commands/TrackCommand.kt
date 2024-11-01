package nl.xandermarc.mc.ride.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands.literal
import nl.xandermarc.mc.lib.editor.EditorManager
import nl.xandermarc.mc.lib.commands.BrigadierCommand
import nl.xandermarc.mc.lib.extensions.execute
import nl.xandermarc.mc.lib.extensions.executePlayer
import nl.xandermarc.mc.lib.extensions.getString
import nl.xandermarc.mc.lib.extensions.stringArgument
import nl.xandermarc.mc.ride.managers.TrackManager
import nl.xandermarc.mc.ride.tracked.TrackEditor

object TrackCommand : BrigadierCommand("track") {
    @Suppress("UnstableApiUsage")
    override val command: LiteralArgumentBuilder<CommandSourceStack> = root
        .then(
            literal("create")
            .then(
                stringArgument("track")
                    .execute {
                        val track = getString("track")
                        if (TrackManager.exists(track)) source.sender.sendPlainMessage("Track $track already exists!")
                        else {
                            TrackManager.create(track)
                            source.sender.sendPlainMessage("Track $track created!")
                        }
                    }
            )
        )
        .then(
            literal("edit")
            .then(
                stringArgument("track")
                    .executePlayer { player ->
                        val track = getString("track")
                        if (EditorManager.hasEditor(player)) {
                            EditorManager.closeEditor(player)
                        } else if (TrackManager.exists(track)) {
                            EditorManager.registerEditor(TrackEditor(player, TrackManager.get(track)!!))
                        } else {
                            source.sender.sendPlainMessage("Track $track does not exist.")
                        }
                    }
            )
            .executePlayer { player ->
                if (EditorManager.hasEditor(player)) {
                    EditorManager.closeEditor(player)
                } else {
                    source.sender.sendPlainMessage("You are not editing any track (use /track edit <name>).")
                }
            }
        )
        .then(
            literal("remove")
                .then(
                    stringArgument("track")
                    .execute {
                        val track = getString("track")
                        if (TrackManager.exists(track)) {
                            TrackManager.remove(track)
                            source.sender.sendPlainMessage("Track $track removed!")
                        } else {
                            source.sender.sendPlainMessage("Track $track does not exist.")
                        }
                    }
                )
        )
}