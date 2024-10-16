package nl.xandermarc.mc.rides.entities

import nl.xandermarc.mc.lib.math.Point
import org.joml.Quaterniond
import org.joml.Vector3d
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

abstract class RideEntity(
    var position: Point,
    val id: Int = nextEntityId.getAndIncrement(),
    val uuid: UUID = UUID.randomUUID()
) {
    companion object { val nextEntityId = AtomicInteger(1) }
    val children = mutableListOf<RideEntity>()

    var parent: RideEntity? = null
    var offset: Vector3d? = null
    var rotation: Quaterniond? = null

    fun move(position: Point) {
    }

    fun addChild(child: RideEntity, offset: Vector3d, rotation: Quaterniond) {
        child.parent = this
        child.offset = offset
        child.rotation = rotation
        children.add(child)
    }

    fun removeChild(child: RideEntity) {
        child.parent = null
        children.remove(child)
    }
}
