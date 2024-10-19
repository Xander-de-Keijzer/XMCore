package nl.xandermarc.mc.lib.extensions

const val DEGREES_TO_RADIANS_D = 0.017453292519943295
const val DEGREES_TO_RADIANS_F = 0.017453292F
const val RADIANS_TO_DEGREES_D = 57.29577951308232
const val RADIANS_TO_DEGREES_F = 57.29578F

fun Double.toRadians() = this * DEGREES_TO_RADIANS_D
fun Double.toDegrees() = this * RADIANS_TO_DEGREES_D

fun Float.toRadians() = this * DEGREES_TO_RADIANS_F
fun Float.toDegrees() = this * RADIANS_TO_DEGREES_F

fun Double.squared() = this * this
fun Double.cubed() = this * this * this
