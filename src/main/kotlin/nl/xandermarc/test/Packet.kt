package nl.xandermarc.test

import nl.xandermarc.test.Packet.RequestListener
import kotlin.random.Random

open class Packet<T : Packet<T>>(
    val id: Int,
    val type: Type,
    val domain: Domain,
    val data: Map<String, Any?>,
    val key: Long = Random.nextLong()
) {
    fun send() {
        TODO()
    }

    fun request(response: (Packet<T>) -> Unit) {
        require(type == Type.REQUEST) { "Packet is not a request packet." }
        requestListeners[key] = RequestListener {
            @Suppress("UNCHECKED_CAST")
            response(it as Packet<T>)
        }
    }

    enum class Type { SEND, REQUEST, RESPONSE, EXCEPTION }
    enum class Domain { CONNECTION, USER, WORLD }

    class StartConnection internal constructor(type: Type, domain: Domain, ww: String, key: Long) :
        Packet<StartConnection>(Types.START_CONNECTION.id, type, domain, mapOf("ww" to ww), key)

    enum class Types(val id: Int, val builder: (Type, Domain, Map<String, Any?>, Long) -> Packet<*>) {
        START_CONNECTION(1, { type, domain, data, key -> StartConnection(type, domain, data["ww"] as String, key) })
    }

    internal fun interface RequestListener<T: Packet<T>> { fun onResponse(packet: Packet<*>) }
    companion object {
        private val requestListeners = mutableMapOf<Long, RequestListener<*>>()
        private fun buildPacket(id: Int, type: Type, domain: Domain, data: Map<String, Any?>, key: Long): Packet<*> {
            Types.entries.forEach {
                if (it.id == id) return it.builder(type, domain, data, key)
            }
            error("Packet with id '$id' does not exist Packet<?>(id=$id, type=$type, domain=$domain, data=$data, key=$key).")
        }
        fun onReceive(id: Int, type: Type, domain: Domain, data: Map<String, Any?>, key: Long) {
            if (requestListeners.containsKey(key)) {
                requestListeners[key]?.onResponse(buildPacket(id, type, domain, data, key))
            }
        }
    }
}
