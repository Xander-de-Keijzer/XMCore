package nl.xandermarc.mc.rides.tracked

import nl.xandermarc.mc.lib.logging.debug
import nl.xandermarc.mc.lib.logging.info

data class Track(
    var name: String,
    val nodes: ArrayList<Int> = arrayListOf()
) {
    val segments = arrayListOf<TrackSegment>()

    fun getSegment(id: Int) = segments.find { it.id == id }

    // TODO
    fun canLoadSegments(): Boolean {
        return true
    }

    // TODO
    fun loadSegments(): Track {
        debug("Loading segments of track $name")
        for (i in (1..6)) {
            segments.add(TrackSegment(i, listOf()))
        }
        return this
    }

    // TODO
    fun generateSegments(): Track {
        debug("Generating segments for track $name")
        return this
    }

    fun loadOrGenerateSegments() =
        if (canLoadSegments()) loadSegments().info("Loaded ${segments.size} segments of track $name")
        else generateSegments().info("Generated ${segments.size} segments for track $name")

    fun isEmpty() = nodes.isEmpty() && segments.isEmpty()
}
