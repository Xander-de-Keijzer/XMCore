package nl.xandermarc.mc.rides.tracked

object TrackManager {
    private val tracks: HashMap<String, Track> = hashMapOf()

    fun getOrCreate(name: String): Track {
        return if (tracks.containsKey(name)) {
            tracks[name]!!
        } else {
            Track()
        }
    }
}