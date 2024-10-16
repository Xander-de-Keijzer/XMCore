package nl.xandermarc.mc.lib.logging

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.logging.Level

object LevelSerializer : KSerializer<Level> {
    override val descriptor = PrimitiveSerialDescriptor("Level", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): Level = Level.parse(decoder.decodeString())
    override fun serialize(encoder: Encoder, value: Level) = encoder.encodeString(value.toString())
}
