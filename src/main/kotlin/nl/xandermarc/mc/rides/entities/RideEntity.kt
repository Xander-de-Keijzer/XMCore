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
    companion object {
        val nextEntityId = AtomicInteger(1)
    }

    private val children = mutableListOf<RideEntity>()

    private var parent: RideEntity? = null
    private var offset: Vector3d? = null
    private var rotation: Quaterniond? = null

    abstract fun move(position: Point)

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
