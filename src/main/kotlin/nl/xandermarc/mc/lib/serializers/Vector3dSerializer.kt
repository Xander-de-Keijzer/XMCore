package nl.xandermarc.mc.lib.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.joml.Vector3d

object Vector3dSerializer : KSerializer<Vector3d> {
    @Serializable
    private class Vector3dSurrogate(val x: Double, val y: Double, val z: Double)

    override val descriptor: SerialDescriptor = Vector3dSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Vector3d) {
        val surrogate = Vector3dSurrogate(value.x, value.y, value.z)
        encoder.encodeSerializableValue(Vector3dSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): Vector3d {
        val surrogate = decoder.decodeSerializableValue(Vector3dSurrogate.serializer())
        return Vector3d(surrogate.x, surrogate.y, surrogate.z)
    }
}
