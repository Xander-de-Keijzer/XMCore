package nl.xandermarc.mc.ride

import nl.xandermarc.mc.lib.area.Area

abstract class AbstractRide<T : Area>(
    val name: String,
    val area: T
) {
    open var state: State = State.DISABLED
        protected set
    abstract fun enable()
    abstract fun disable()
    abstract fun reset()
    abstract fun update()

    protected abstract fun load()
    protected abstract fun remove()

    enum class State {
        DISABLED,
        ENABLING,
        ENABLED,
        DISABLING
    }
}
