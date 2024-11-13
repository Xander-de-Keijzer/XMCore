package nl.xandermarc.mc.ride

import nl.xandermarc.mc.lib.interfaces.NestedList
import nl.xandermarc.mc.ride.entity.RideObject

data class Cart(
    val sizeFront: Double,
    val sizeRear: Double,
    val objects: List<RideObject>
) : NestedList<Cart> {
    constructor(
        sizeFront: Double,
        sizeRear: Double,
        vararg objects: RideObject
    ) : this(sizeFront, sizeRear, objects.toList())
    override fun nested() = listOf(this)
    fun copy(n: Int) = NestedList { List(n) { this@Cart.copy() } }
}
