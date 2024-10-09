package nl.xandermarc.mc.lib.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import nl.xandermarc.mc.Player

object PlayerSerializer : KSerializer<Player> {
    override val descriptor = PrimitiveSerialDescriptor("Player", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): Player = Player(decoder.decodeString())
    override fun serialize(encoder: Encoder, value: Player) = encoder.encodeString(value.name)
}
