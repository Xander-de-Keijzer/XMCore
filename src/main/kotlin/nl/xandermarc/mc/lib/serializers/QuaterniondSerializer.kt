package nl.xandermarc.mc.lib.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.joml.Quaterniond

object QuaterniondSerializer : KSerializer<Quaterniond> {
    @Serializable
    private class QuaterniondSurrogate(val x: Double, val y: Double, val z: Double, val w: Double)

    override val descriptor: SerialDescriptor = QuaterniondSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Quaterniond) {
        val surrogate = QuaterniondSurrogate(value.x, value.y, value.z, value.w)
        encoder.encodeSerializableValue(QuaterniondSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): Quaterniond {
        val surrogate = decoder.decodeSerializableValue(QuaterniondSurrogate.serializer())
        return Quaterniond(surrogate.x, surrogate.y, surrogate.z, surrogate.w)
    }
}
