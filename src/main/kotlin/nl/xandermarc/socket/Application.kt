package nl.xandermarc.socket

import io.ktor.server.application.*
import nl.xandermarc.socket.plugins.configureRouting
import nl.xandermarc.socket.plugins.configureSerialization
import nl.xandermarc.socket.plugins.configureSockets

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSockets()
    configureSerialization()
    configureRouting()
}
