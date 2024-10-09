package nl.xandermarc.mc.lib.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf

@OptIn(ExperimentalSerializationApi::class)
class SerializationCodec(
    @ExperimentalSerializationApi
    val useProtoBuf: Boolean = false
) {
    val json = Json { prettyPrint = false }
    val proto = ProtoBuf

    inline fun <reified T> encodeToFile(data: T) =
        if (useProtoBuf)
            encodeToProto(data)
        else
            encodeToJson(data)

    inline fun <reified T> encodeToJson(data: T) =
        json.encodeToString(data).toByteArray()

    inline fun <reified T> encodeToProto(data: T) =
        proto.encodeToByteArray(data)

    inline fun <reified T> decodeFromBytes(bytes: ByteArray): T? {
        if (useProtoBuf)
            return proto.decodeFromByteArray<T>(bytes)

        return json.decodeFromString(bytes.decodeToString())
    }

    inline fun <reified T> decodeFromJson(string: String): T =
        json.decodeFromString<T>(string)

    inline fun <reified T> decodeFromProto(bytes: ByteArray): T =
        proto.decodeFromByteArray<T>(bytes)
}
