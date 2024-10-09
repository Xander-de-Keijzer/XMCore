package nl.xandermarc.mc.rides.tracked

import nl.xandermarc.mc.lib.math.Vec

data class TrackSegment(
    val id: Int,
    val points: List<Vec>
)
