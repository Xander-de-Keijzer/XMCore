package nl.xandermarc.test.network

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import nl.xandermarc.mc.lib.data.Globals
import nl.xandermarc.mc.lib.extensions.launchAsync
import nl.xandermarc.mc.lib.serializers.MapSerializer
import java.net.SocketException
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration

enum class Packet(
    private val id: Int,
    private val domain: Domain,
    private val type: Type,
) {
    START_CONNECTION(0, Domain.CONNECTION, Type.POST),
    DEFINE_USER(1, Domain.USER, Type.POST),
    CREATE_SESSION(2, Domain.SESSION, Type.REQUEST),
    UPDATE_PLAYERS(3, Domain.WORLD, Type.POST),
    ;

    fun build(
        data: Map<String, Any>,
        uid: Int = atomic.getAndIncrement(),
        sender: Connector = Connector.MINECRAFT,
        receiver: Connector = Connector.WEBSERVER,
        type: Type = this.type
    ) = Data(
        this.id,
        this.domain,
        type,
        uid,
        sender,
        receiver,
        data
    )

    enum class Connector {
        MINECRAFT,
        WEBSERVER
    }
    enum class Type {
        EXCEPTION,
        POST,
        REQUEST,
        RESPONSE
    }
    enum class Domain {
        CONNECTION,
        USER,
        SESSION,
        WORLD
    }

    @Serializable
    data class Data(
        val id: Int,
        val domain: Domain,
        var type: Type,
        val uid: Int,
        val sender: Connector,
        val receiver: Connector,
        @Serializable(with= MapSerializer::class)
        val data: Map<String, @Contextual Any?>
    ) {
        var isSent = false
        val canSend = sender == Connector.MINECRAFT
        val canReceive = type == Type.REQUEST || type == Type.POST
        val name = Packet.decode(id)

        fun send(): Data {
            if (!canSend) throw IllegalAccessException("Packet '$this' can't be send (sender = $sender).")
            isSent = true
            Packet.send(this)
            return this
        }
        fun onResponse(listener: Listener,) {
            onResponse(listener, Duration.parse("30s"))
        }
        fun onResponse(listener: Listener, timeout: Duration) {
            if (!canReceive) throw IllegalAccessException("Packet '$this' is not a request or post, and thus can't receive a response (sender = $sender).")
            if (!isSent) send()
            listeners.add(ListenerDelegate(uid, listener, timeout))
        }
        init {
            if (name == null) throw IllegalArgumentException("Packet '$this' is invalid, packet id $id does not exist.")
        }
    }
    private data class ListenerDelegate(
        val uid: Int,
        val listener: Listener,
        val timeout: Duration = Duration.parse("30s")
    ) {
        val time: Instant = Clock.System.now()
    }
    fun interface Listener {
        fun onReceive(packetData: Data)
    }
    companion object {
        val client = HttpClient(CIO) { install(WebSockets) }
        var socketEnabled: Boolean = true
        private val atomic = AtomicInteger(0)
        private val listeners = mutableListOf<ListenerDelegate>()
        fun decode(ordinal: Int) = entries.firstOrNull { it.ordinal == ordinal }

        fun receive(data: Data) {
            Globals.logger.info("Receiving data ${Globals.json.encodeToString(data)}")

            if (data.type == Type.RESPONSE || data.type == Type.EXCEPTION) {
                val listening = listeners.filter { it.uid == data.uid }
                if (listening.isEmpty()) {
                    if (data.type == Type.RESPONSE) {
                        Globals.logger.warning("Received data $data which is a response packet but no listeners were registered for it.")
                    } else {
                        Globals.logger.info("Received exception $data")
                    }
                }
                listening.forEach { listener ->
                    listener.listener.onReceive(data)
                    listeners.remove(listener)
                }
            }
        }
        fun send(data: Data) {
            Globals.logger.info("Sending data ${Globals.json.encodeToString(data)}")
            launchAsync {
                // TODO Actually send data
                receive(data)
            }
        }
        fun purgeTimout() {
            val now = Clock.System.now()
            listeners.removeAll { now.minus(it.time) > it.timeout }
        }
    }
    fun tryStart(client: HttpClient, block: suspend DefaultClientWebSocketSession.() -> Unit) {
        launchAsync {
            try {
                client.webSocket(HttpMethod.Get, "127.0.0.1", 8080, "/ws", {}, block)
            } catch (e: SocketException) {
                println("[SOCKET]: Exception '${e.message}' of type ${e::class.simpleName}")
                delay(1000)
                test()
                println("[SOCKET] Retrying connection")
            }
        }
    }
    fun test() {
        tryStart(client) {
            while (socketEnabled) {
                send("Test")
                println("Sent: Test")
                launchAsync {
                    for (frame in incoming) {
                        println("Received: $frame")
                    }
                }
                delay(1000)
            }

            if (isActive) {
                close(CloseReason(CloseReason.Codes.NORMAL, "Client disconnected"))
                println("[SOCKET] Closed by client")
            } else {
                delay(1000)
                test()
                println("[SOCKET] Retrying connection")
            }
        }
    }
}
