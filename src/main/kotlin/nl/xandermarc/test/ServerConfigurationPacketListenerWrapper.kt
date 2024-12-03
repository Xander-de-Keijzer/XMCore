package nl.xandermarc.test

import net.minecraft.network.protocol.common.ServerboundResourcePackPacket
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener
import net.minecraft.server.network.ConfigurationTask
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl
import kotlin.reflect.full.memberProperties

class ServerConfigurationPacketListenerWrapper(
    private val listener: ServerConfigurationPacketListenerImpl
) : ServerConfigurationPacketListener by listener {
    override fun handleResourcePackResponse(packet: ServerboundResourcePackPacket) {
        val currentTask = ServerConfigurationPacketListenerImpl::class.memberProperties.first {
            it.name == "currentTask"
        }.call(listener) as ConfigurationTask
        if (currentTask.type() == CustomResourcePackConfigTask.type) {
            println("Ignored custom type")
        } else {
            listener.handleResourcePackResponse(packet)
        }
    }
}
