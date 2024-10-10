package nl.xandermarc.mc.rides

abstract class Ride(val name: String) {
    var enabled = false
    fun reset() {
        remove()
        init()
    }
    protected open fun initAsync() {}
    protected abstract fun init()
    abstract fun remove()
    abstract fun update()
    open fun updateAsync() {}
}
