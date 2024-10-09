package nl.xandermarc.mc.lib.math

fun evaluate(p0: Vec, p1: Vec, p2: Vec, p3: Vec, t: Double): Vec {
    val oneMinusT = 1.0 - t
    val oneMinusT2 = oneMinusT * oneMinusT
    val oneMinusT3 = oneMinusT2 * oneMinusT
    val t2 = t * t
    val t3 = t2 * t

    val term0 = p0 * oneMinusT3
    val term1 = p1 * (3 * oneMinusT2 * t)
    val term2 = p2 * (3 * oneMinusT * t2)
    val term3 = p3 * t3

    return term0 + term1 + term2 + term3
}

fun evaluate(p1: Vec, p2: Vec, p3: Vec, t: Double): Vec {
    val oneMinusT = 1.0 - t
    val oneMinusT2 = oneMinusT * oneMinusT
    val t2 = t * t
    val t3 = t2 * t

    val term1 = p1 * (3 * oneMinusT2 * t)
    val term2 = p2 * (3 * oneMinusT * t2)
    val term3 = p3 * t3

    return term1 + term2 + term3
}
