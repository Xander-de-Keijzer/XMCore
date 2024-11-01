package nl.xandermarc.mc.ride.managers

import nl.xandermarc.mc.ride.tracked.track.Track
import java.util.concurrent.ConcurrentHashMap

object TrackManager {
    private val tracks = ConcurrentHashMap<String, Track>()

    fun exists(trackName: String) = tracks.containsKey(trackName)
    fun get(trackName: String) = tracks[trackName]
    fun create(trackName: String) = tracks.computeIfAbsent(trackName) { Track(trackName) }
    fun remove(trackName: String) = tracks.remove(trackName)
    fun remove(track: Track) = tracks.remove(track.name)
    fun purgeEmpty() = tracks.entries.removeIf { it.value.isEmpty() }

    fun disable() {
        // TODO Save tracks to persistent storage
    }
}
