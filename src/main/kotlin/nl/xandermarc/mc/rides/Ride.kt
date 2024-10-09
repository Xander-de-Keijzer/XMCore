package nl.xandermarc.mc.rides

abstract class Ride(val name: String) {
    var enabled = false
    fun register() {
        if (!enabled) {
            init()
            enabled = true
        }
        RideManager.register(this)
    }
    fun unregister() {
        if (enabled) {
            remove()
            enabled = false
        }
        RideManager.unregister(this)
    }
    fun reset() {
        unregister()
        register()
    }
    protected abstract fun init()
    protected abstract fun remove()
    abstract fun update()
    open fun updateAsync() {}
}
