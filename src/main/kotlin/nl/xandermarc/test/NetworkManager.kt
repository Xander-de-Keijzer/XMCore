package nl.xandermarc.test

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import nl.xandermarc.mc.lib.data.Globals
import nl.xandermarc.mc.lib.extensions.warn
import kotlin.time.Duration

object NetworkManager {
    private data class PacketListener(
        val key: List<Int>,
        val block: (List<Any>) -> Unit,
        val started: Instant = Clock.System.now(),
    )
    private val listeners = mutableListOf<PacketListener>()

    fun onResponse(key: List<Int>, block: (List<Any>) -> Unit) {
        listeners.add(
            PacketListener(
                key,
                block
            )
        )
    }
    fun purge() {
        val now = Clock.System.now()
        listeners.toList().filter {
            (now - it.started) > Duration.parse("2m")
        }.forEach {
            listeners.remove(it)
            Globals.logger.warn { "Listener for packet ${it.key} timed out." }
        }
    }
}
