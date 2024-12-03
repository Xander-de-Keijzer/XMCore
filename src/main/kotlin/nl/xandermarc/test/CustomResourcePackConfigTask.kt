package nl.xandermarc.test

import io.papermc.paper.adventure.PaperAdventure
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket
import net.minecraft.server.network.ConfigurationTask
import nl.xandermarc.mc.lib.data.Globals
import java.util.*
import java.util.function.Consumer

class CustomResourcePackConfigTask : ConfigurationTask {
    companion object {
        val type = ConfigurationTask.Type("custom_resource_pack")
    }

    override fun start(sender: Consumer<Packet<*>>) {
        sender.accept(
            ClientboundResourcePackPushPacket(
                UUID.randomUUID(),
                "https://cdn.modrinth.com/data/w0TnApzs/versions/UIpuBfGX/Faithful%2032x%20-%201.21.3.zip",
                "test",
                true,
                Optional.of(PaperAdventure.asVanilla(Globals.message.deserialize("<red>ResourcePack is required")))
            )
        )
    }

    override fun type(): ConfigurationTask.Type = type
}