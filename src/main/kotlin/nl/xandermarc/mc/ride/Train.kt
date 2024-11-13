package nl.xandermarc.mc.ride

import nl.xandermarc.mc.lib.interfaces.NestedList

data class Train(
    val carts: List<Cart>,
    var position: Track.Position?
) {
    var speed: Double = 0.0
    fun remove() {}
    class Builder(
        private vararg val entries: NestedList<Cart>
    ) {
        fun build(
            position: Track.Position
        ): Train = Train(entries.flatMap { it.nested() }, position)
    }
}
