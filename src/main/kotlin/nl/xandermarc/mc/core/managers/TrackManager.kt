package nl.xandermarc.mc.core.managers

import nl.xandermarc.mc.lib.utils.Manager
import nl.xandermarc.mc.ride.tracked.track.Track
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.ConcurrentHashMap

object TrackManager : Manager {
    private val tracks = ConcurrentHashMap<String, Track>()

    fun exists(trackName: String) = tracks.containsKey(trackName)
    fun get(trackName: String) = tracks[trackName]
    fun create(trackName: String) = tracks.computeIfAbsent(trackName) { Track(trackName) }
    fun remove(trackName: String) = tracks.remove(trackName)
    fun remove(track: Track) = tracks.remove(track.name)
    fun purgeEmpty() = tracks.entries.removeIf { it.value.isEmpty() }

    override fun enable(plugin: JavaPlugin) {
        // TODO Load tracks from persistent storage
    }

    override fun disable() {
        // TODO Save tracks to persistent storage
    }
}
