package nl.xandermarc.mc.lib.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.joml.Vector3f

object Vector3fSerializer : KSerializer<Vector3f> {
    @Serializable
    private class Vector3fSurrogate(val x: Float, val y: Float, val z: Float)

    override val descriptor: SerialDescriptor = Vector3fSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Vector3f) {
        val surrogate = Vector3fSurrogate(value.x, value.y, value.z)
        encoder.encodeSerializableValue(Vector3fSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): Vector3f {
        val surrogate = decoder.decodeSerializableValue(Vector3fSurrogate.serializer())
        return Vector3f(surrogate.x, surrogate.y, surrogate.z)
    }
}
