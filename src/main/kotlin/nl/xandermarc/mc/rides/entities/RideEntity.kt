package nl.xandermarc.mc.rides.entities

import nl.xandermarc.mc.lib.math.Vec

abstract class RideEntity {
    var parent: RideEntity? = null
        private set
    var offset: Vec? = null
        private set
    private val children = arrayListOf<RideEntity>()

    fun children(): List<RideEntity> = children.toList()
    fun addChild(offset: Vec, child: RideEntity) {
        child.parent = this
        child.offset = offset
        children.add(child)
    }
    fun removeChild(child: RideEntity) {
        child.parent = null
        child.offset = null
        children.remove(child)
    }
}