package nl.xandermarc.mc.rides.tracked

import io.papermc.paper.command.brigadier.Commands.literal
import nl.xandermarc.mc.lib.commands.Command
import nl.xandermarc.mc.lib.commands.execute
import nl.xandermarc.mc.lib.commands.getString
import nl.xandermarc.mc.lib.commands.stringArgument

object TrackManager : Command("track") {
    private val tracks = mutableListOf<Track>()

    fun exists(trackName: String) = synchronized(tracks) { tracks.any { it.name == trackName } }
    fun get(trackName: String) = synchronized(tracks) { tracks.firstOrNull { it.name == trackName } }
    fun create(trackName: String) = synchronized(tracks) { Track(trackName).also { tracks.add(it) } }
    fun getOrCreate(trackName: String) = get(trackName) ?: create(trackName)
    fun remove(trackName: String) = synchronized(tracks) { tracks.removeAll { it.name == trackName } }
    fun remove(track: Track) = synchronized(tracks) { tracks.remove(track) }
    fun purgeEmpty() = synchronized(tracks) { tracks.removeAll { it.isEmpty() } }

    override val command = root
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
}
