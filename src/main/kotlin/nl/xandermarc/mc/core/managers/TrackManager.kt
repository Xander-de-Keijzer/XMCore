package nl.xandermarc.mc.core.managers

import io.ktor.util.collections.*
import nl.xandermarc.mc.ride.Track

object TrackManager {
    private val tracks = ConcurrentSet<Track>()

    fun has(trackName: String) = tracks.any { it.name == trackName }
    fun has(track: Track) = tracks.contains(track)
    fun get(trackName: String) = tracks.firstOrNull { it.name == trackName }
    fun put(track: Track) = tracks.add(track)
    fun getOrCreate(trackName: String) = get(trackName) ?: Track(trackName).apply { tracks.add(this) }
    fun remove(track: Track) = tracks.remove(track)
    fun remove(trackName: String) = get(trackName)?.apply { remove(this) }
    fun names() = tracks.map { it.name }

    fun purgeEmpty() = tracks.removeIf { it.nodes.isEmpty() }
}
