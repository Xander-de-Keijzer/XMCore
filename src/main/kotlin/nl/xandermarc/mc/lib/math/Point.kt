package nl.xandermarc.mc.lib.math

import kotlinx.serialization.Serializable
import org.joml.Quaterniond
import org.joml.Vector3d

@Serializable
data class Point(
    @Serializable(with = Vector3dSerializer::class)
    val location: Vector3d,
    @Serializable(with = QuaterniondSerializer::class)
    val rotation: Quaterniond,
)
