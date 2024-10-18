package nl.xandermarc.mc.lib.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.joml.Quaternionf

object QuaternionfSerializer : KSerializer<Quaternionf> {
    @Serializable
    private class QuaternionfSurrogate(val x: Float, val y: Float, val z: Float, val w: Float)

    override val descriptor: SerialDescriptor = QuaternionfSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Quaternionf) {
        val surrogate = QuaternionfSurrogate(value.x, value.y, value.z, value.w)
        encoder.encodeSerializableValue(QuaternionfSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): Quaternionf {
        val surrogate = decoder.decodeSerializableValue(QuaternionfSurrogate.serializer())
        return Quaternionf(surrogate.x, surrogate.y, surrogate.z, surrogate.w)
    }
}
