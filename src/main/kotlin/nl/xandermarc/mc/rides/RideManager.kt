package nl.xandermarc.mc.rides

import nl.xandermarc.mc.lib.extensions.EnabledState
import nl.xandermarc.mc.lib.logging.debug
import nl.xandermarc.mc.lib.logging.info
import nl.xandermarc.mc.lib.logging.warn

object RideManager {
    val rides = mutableListOf<Ride>()
    val rideNames
        get() = rides.map { it.name }.toList()

    fun register(ride: Ride) {
        if (isRegistered(ride)) return
        rides.add(ride).info { "Registered ride ${ride.name} (success=$this)" }
        if (ride.state != EnabledState.DISABLED) {
            warn("Ride ${ride.name} was not disabled whilst registering")
        } else {
            ride.enable()
        }
    }

    fun register(vararg rides: Ride) = rides.forEach { register(it) }
    fun isRegistered(rideName: String) =
        rides.any { it.name == rideName }.debug { "Ride $rideName (registered=$this)" }

    private fun isRegistered(ride: Ride) =
        rides.contains(ride).debug { "Ride ${ride.name} (registered=$this)" }

    fun unregister(rideName: String) {
        rides.find { it.name == rideName }?.let { unregister(it) }
    }

    private fun unregister(ride: Ride) {
        if (!isRegistered(ride)) return
        ride.disable()
        rides.remove(ride).info("Unregistered ride ${ride.name} (success=$this)")
    }

    fun update() {
        rides.forEach { it.update() }
    }

    suspend fun updateAsync() {
        rides.forEach { it.updateAsync() }
    }
}
