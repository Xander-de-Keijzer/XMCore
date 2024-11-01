package nl.xandermarc.mc.ride.managers

import nl.xandermarc.mc.lib.data.Globals
import nl.xandermarc.mc.lib.extensions.debug
import nl.xandermarc.mc.lib.extensions.info
import nl.xandermarc.mc.lib.extensions.warn
import nl.xandermarc.mc.ride.AsyncRide
import nl.xandermarc.mc.ride.AbstractRide

object RideManager {
    private val rides = mutableListOf<AbstractRide<*>>()
    private fun check(test: Int) {
        Globals.logger.info("TEST $test!")
    }

    internal fun register(ride: AbstractRide<*>): Boolean {
        if (rides.any { it.name == ride.name }) return false.debug { "Ride ${ride.name} is already registered" }
        ride.area.loadChunks(true)
        return rides.add(ride).info { "Registered ride ${ride.name}" }
    }

    internal fun unregister(rideName: String) {
        val remove = rides.filter { it.name == rideName }
        if (remove.isEmpty()) debug("Ride $rideName was not registered.")
        if (remove.size != 1) { warn("Multiple rides with the name $rideName were registered ($remove)") }
        remove.forEach {
            it.area.releaseChunks()
            rides.remove(it)
            info("Removed ride $rideName($it)")
        }
    }

    fun update() {
        rides.forEach { it.update() }
    }

    suspend fun updateAsync() {
        rides.filterIsInstance<AsyncRide<*>>().forEach { it.updateAsync() }
    }

    fun disable() {
        rides.forEach { it.disable() }
    }
}
