package nl.xandermarc.mc.rides.tracked

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands.literal
import nl.xandermarc.mc.lib.commands.*
import nl.xandermarc.mc.rides.editor.EditorManager

object TrackManager : Command("track") {
    private val tracks = mutableListOf<Track>()

    private fun exists(trackName: String) = synchronized(tracks) { tracks.any { it.name == trackName } }
    fun get(trackName: String) = synchronized(tracks) { tracks.firstOrNull { it.name == trackName } }
    private fun create(trackName: String) = synchronized(tracks) { Track(trackName).also { tracks.add(it) } }
    fun getOrCreate(trackName: String) = get(trackName) ?: create(trackName)
    fun remove(trackName: String) = synchronized(tracks) { tracks.removeAll { it.name == trackName } }
    fun remove(track: Track) = synchronized(tracks) { tracks.remove(track) }
    fun purgeEmpty() = synchronized(tracks) { tracks.removeAll { it.isEmpty() } }

    @Suppress("UnstableApiUsage")
    override val command: LiteralArgumentBuilder<CommandSourceStack> = root
        .then(literal("create")
            .then(stringArgument("track")
                .execute {
                    val track = getString("track")
                    if (exists(track)) source.sender.sendPlainMessage("Track $track already exists!")
                    else {
                        create(track)
                        source.sender.sendPlainMessage("Track $track created!")
                    }
                }
            )
        )
        .then(literal("edit")
            .then(stringArgument("track")
                .executePlayer { player ->
                    val track = getString("track")
                    if (EditorManager.isUsingEditor(player)) {
                        EditorManager.stopEditor(player)
                    } else if (exists(track)) {
                        EditorManager.registerEditor(TrackEditor(player, get(track)!!))
                    } else {
                        source.sender.sendPlainMessage("Track $track does not exists.")
                    }
                }
            )
            .executePlayer { player ->
                if (EditorManager.isUsingEditor(player)) {
                    EditorManager.stopEditor(player)
                } else {
                    source.sender.sendPlainMessage("You are not editing any track (use /track edit <name>).")
                }
            }
        )
}
