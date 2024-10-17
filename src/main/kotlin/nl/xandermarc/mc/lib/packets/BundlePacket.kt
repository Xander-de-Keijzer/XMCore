package nl.xandermarc.mc.lib.packets

import net.minecraft.network.protocol.game.ClientboundBundlePacket

class BundlePacket(
    packets: Iterable<AbstractPacket<*>>
) : AbstractPacket<ClientboundBundlePacket>(
    ClientboundBundlePacket(packets.map { it.packet })
) {
    constructor(vararg packets: AbstractPacket<*>) : this(packets.toList())
}
