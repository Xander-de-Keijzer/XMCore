package nl.xandermarc.mc.ride.tracked.track

import kotlinx.serialization.Serializable
import nl.xandermarc.mc.lib.serializers.Vector3dSerializer
import org.joml.Vector3d

@Serializable
data class Node(
    @Serializable(with = Vector3dSerializer::class)
    val location: Vector3d,
    @Serializable(with = Vector3dSerializer::class)
    val rotation: Vector3d = Vector3d(),
    var radius: Double = 3.0
)
