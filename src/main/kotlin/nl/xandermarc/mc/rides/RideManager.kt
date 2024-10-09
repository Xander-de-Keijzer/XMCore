package nl.xandermarc.mc.rides

import nl.xandermarc.mc.lib.logging.debug
import nl.xandermarc.mc.lib.logging.info
import nl.xandermarc.mc.lib.logging.warn

object RideManager {
    val rides = arrayListOf<Ride>()
    val rideNames
        get() = rides.map { it.name }.toTypedArray()

    fun register(ride: Ride) {
        if (!ride.enabled) ride.register().warn("Ride ${ride.name} was not enabled whilst registering")
        if (!isRegistered(ride)) rides.add(ride).info { "Registered ride ${ride.name} (success=$this)" }
    }
    fun isRegistered(rideName: String) =
        rides.any { it.name == rideName }.debug { "Ride $rideName (registered=$this)" }
    fun isRegistered(ride: Ride) =
        rides.contains(ride).debug { "Ride ${ride.name} (registered=$this)" }
    fun unregister(rideName: String) {
        rides.find { it.name == rideName }?.let { unregister(it) }
    }
    fun unregister(ride: Ride) {
        if (ride.enabled) ride.warn("Ride ${ride.name} was enabled whilst unregistering").unregister()
        if (isRegistered(ride)) rides.remove(ride).apply {
            if (this) info("Unregistered ride ${ride.name}")
            else error("Failed to unregister ride ${ride.name}")
        }
    }
    fun update() {
        rides.forEach { it.update() }
    }
    fun updateAsync() {
        rides.forEach { it.updateAsync() }
    }
}
