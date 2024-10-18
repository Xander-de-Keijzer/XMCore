package nl.xandermarc.mc.lib.extensions

const val DEGREES_TO_RADIANS_D = 0.017453292519943295
const val DEGREES_TO_RADIANS_F = 0.017453292F
const val RADIANS_TO_DEGREES_D = 57.29577951308232
const val RADIANS_TO_DEGREES_F = 57.29578F

fun Double.toRadians() = Math.toRadians(this)
fun Double.toDegrees() = Math.toDegrees(this)

fun Float.toRadians() = Math.toRadians(this.toDouble()).toFloat()
fun Float.toDegrees() = Math.toDegrees(this.toDouble()).toFloat()

fun Double.squared() = this * this
fun Double.cubed() = this * this * this
