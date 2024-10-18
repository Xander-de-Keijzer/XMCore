package nl.xandermarc.mc.ride.tracked.track

object TrackManager {
    private val tracks = mutableListOf<Track>()

    fun exists(trackName: String) = synchronized(tracks) { tracks.any { it.name == trackName } }
    fun get(trackName: String) = synchronized(tracks) { tracks.firstOrNull { it.name == trackName } }
    fun create(trackName: String) = synchronized(tracks) { Track(trackName).also { tracks.add(it) } }
    fun getOrCreate(trackName: String) = get(trackName) ?: create(trackName)
    fun remove(trackName: String) = synchronized(tracks) { tracks.removeAll { it.name == trackName } }
    fun remove(track: Track) = synchronized(tracks) { tracks.remove(track) }
    fun purgeEmpty() = synchronized(tracks) { tracks.removeAll { it.isEmpty() } }
}
