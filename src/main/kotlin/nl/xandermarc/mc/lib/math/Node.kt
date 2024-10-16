package nl.xandermarc.mc.lib.math

import kotlinx.serialization.Serializable
import org.joml.Vector3d

@Serializable
data class Node(
    @Serializable(with = Vector3dSerializer::class)
    val location: Vector3d,
    @Serializable(with = Vector3dSerializer::class)
    val rotation: Vector3d = Vector3d(),
    var radius: Double = 3.0
)
