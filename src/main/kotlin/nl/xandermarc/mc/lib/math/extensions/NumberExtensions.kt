package nl.xandermarc.mc.lib.math.extensions

fun Double.toRadians(): Double {
    return Math.toRadians(this)
}

fun Double.squared() = this * this
fun Double.cubed() = this * this * this
