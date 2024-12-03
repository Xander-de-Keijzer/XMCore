package nl.xandermarc.mc.lib.entities

import net.minecraft.world.entity.EntityType
import nl.xandermarc.mc.lib.packets.BundlePacket
import nl.xandermarc.mc.lib.packets.Packet
import nl.xandermarc.mc.lib.packets.entities.AddEntityPacket
import nl.xandermarc.mc.lib.packets.entities.RemoveEntitiesPacket
import org.bukkit.entity.Player
import org.joml.Vector3d
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

abstract class Entity(
    val type: EntityType<*>,
    var location: Vector3d = Vector3d()
) {
    val id = nextId
    val uuid: UUID = UUID.randomUUID()

    fun showTo(player: Player) {
        BundlePacket(
            AddEntityPacket(id, type, location, uuid),
            *onSpawn()
        ).send(player)
    }

    abstract fun onSpawn(): Array<out Packet<*>>

    fun removeFrom(player: Player) {
        RemoveEntitiesPacket(id).send(player)
    }

    companion object {
        private val atomicInt = AtomicInteger(1)
        val nextId: Int get() = atomicInt.getAndIncrement()
        private val random = Random(926337335345937)
        val nextIdRange: Int get() = random.nextInt(Int.MIN_VALUE, 0)
    }
}
