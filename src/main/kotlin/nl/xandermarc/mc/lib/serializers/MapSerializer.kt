package nl.xandermarc.mc.lib.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

object MapSerializer : KSerializer<Map<String, Any?>> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("MapSerializer") {
        element("key", PrimitiveSerialDescriptor("key", PrimitiveKind.STRING))
        element("value", buildClassSerialDescriptor("value"))
    }

    override fun serialize(encoder: Encoder, value: Map<String, Any?>) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw SerializationException("This serializer only supports JSON format.")

        // Convert the map to a JSON object
        val jsonObject = JsonObject(value.mapValues { (_, v) -> encodeToJsonElement(v) })
        jsonEncoder.encodeJsonElement(jsonObject)
    }

    override fun deserialize(decoder: Decoder): Map<String, Any?> {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw SerializationException("This serializer only supports JSON format.")

        // Decode the JSON object
        val jsonObject = jsonDecoder.decodeJsonElement() as? JsonObject
            ?: throw SerializationException("Expected JsonObject for Map deserialization.")

        return jsonObject.mapValues { (_, v) -> decodeFromJsonElement(v) }
    }

    private fun encodeToJsonElement(value: Any?): JsonElement {
        @Suppress("UNCHECKED_CAST")
        return when (value) {
            null -> JsonNull
            is Number -> JsonPrimitive(value)
            is String -> JsonPrimitive(value)
            is Boolean -> JsonPrimitive(value)
            is Map<*, *> -> JsonObject((value as Map<String, Any?>).mapValues { (_, v) -> encodeToJsonElement(v) })
            else -> throw SerializationException("Unsupported type for serialization: ${value::class}")
        }
    }

    private fun decodeFromJsonElement(element: JsonElement): Any? {
        return when (element) {
            is JsonNull -> null
            is JsonPrimitive -> when {
                element.isString -> element.content
                element.booleanOrNull != null -> element.boolean
                element.intOrNull != null -> element.int
                element.longOrNull != null -> element.long
                element.floatOrNull != null -> element.float
                element.doubleOrNull != null -> element.double
                else -> throw SerializationException("Unsupported primitive type: $element")
            }
            is JsonObject -> element.mapValues { (_, v) -> decodeFromJsonElement(v) }
            else -> throw SerializationException("Unsupported JsonElement type: ${element::class}")
        }
    }
}
