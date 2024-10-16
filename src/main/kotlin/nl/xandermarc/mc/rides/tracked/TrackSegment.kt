package nl.xandermarc.mc.rides.tracked

import kotlinx.serialization.Serializable
import nl.xandermarc.mc.lib.math.path.BezierPath

@Serializable
data class TrackSegment(
    val path: BezierPath,
    var aConnected: Int? = null,
    var bConnected: Int? = null,
)
