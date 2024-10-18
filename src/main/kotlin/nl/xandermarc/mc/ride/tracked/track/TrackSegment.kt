package nl.xandermarc.mc.ride.tracked.track

import kotlinx.serialization.Serializable
import nl.xandermarc.mc.lib.path.BezierPath

@Serializable
data class TrackSegment(
    val path: BezierPath,
    var aConnected: Int? = null,
    var bConnected: Int? = null,
)
