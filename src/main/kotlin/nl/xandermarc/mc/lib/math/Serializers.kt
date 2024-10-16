package nl.xandermarc.mc.lib.math

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.joml.Quaterniond
import org.joml.Quaternionf
import org.joml.Vector3d
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
