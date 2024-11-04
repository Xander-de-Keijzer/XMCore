package nl.xandermarc.mc.ride

import nl.xandermarc.mc.lib.area.Area
import nl.xandermarc.mc.lib.utils.Manager

abstract class AbstractRide<T : Area>(
    val name: String,
    val area: T
) : Manager {
    open var state: State = State.DISABLED
        protected set
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
