package nl.xandermarc.mc.rides.tracked

import kotlinx.serialization.Serializable
import nl.xandermarc.mc.lib.math.Vec

@Serializable
data class Node(
    val location: Vec = Vec(),
    var yaw: Double = 0.0,
    var pitch: Double = 0.0,
    var roll: Double = 0.0,
    var radius: Double = 3.0,
)