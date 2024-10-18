package nl.xandermarc.mc.ride

import nl.xandermarc.mc.lib.extensions.debug
import nl.xandermarc.mc.lib.extensions.info

object RideManager {
    private val rides = mutableListOf<Ride>()

    internal fun register(ride: Ride): Boolean {
        if (rides.any { it.name == ride.name }) return false.debug { "Ride ${ride.name} is already registered" }
        return rides.add(ride).info { "Registered ride ${ride.name}" }
    }

    internal fun unregister(rideName: String) {
        if (rides.removeAll { it.name == rideName }) {
            info("Ride $rideName has been unregistered")
        } else debug("Ride $rideName was not registered.")
    }

    fun update() {
        rides.forEach { it.update() }
    }

    suspend fun updateAsync() {
        rides.forEach { it.updateAsync() }
    }

    fun disable() {
        rides.forEach { it.disable() }
    }
}
