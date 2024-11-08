package nl.xandermarc.mc.ride.tracked.listeners

import nl.xandermarc.mc.ride.tracked.Cart

fun interface CartListener {
    fun onCartTick(cart: Cart)
}
