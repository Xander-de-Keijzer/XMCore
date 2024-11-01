package nl.xandermarc.mc.ride.tracked.track

import kotlinx.serialization.Serializable
import nl.xandermarc.mc.lib.serializers.QuaterniondSerializer
import nl.xandermarc.mc.lib.serializers.Vector3dSerializer
import org.joml.Quaterniond
import org.joml.Vector3d

@Serializable
data class Point(
    @Serializable(with = Vector3dSerializer::class)
    val location: Vector3d,
    @Serializable(with = QuaterniondSerializer::class)
    val rotation: Quaterniond,
)
