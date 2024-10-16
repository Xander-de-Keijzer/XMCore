package nl.xandermarc.mc.lib.logging

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
sealed class LogItem(
    val instant: Instant = Clock.System.now()
) {
    val key: String by lazy {
        (this::class.qualifiedName!!).split(".").takeLast(2).joinToString(".").lowercase()
    }

    override fun toString(): String = Json.encodeToString(serializer(), this)

    companion object {
        fun from(string: String): LogItem =
            Json.decodeFromString(serializer(), string)
    }
}
