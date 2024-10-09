package nl.xandermarc.mc.lib.math.path

import nl.xandermarc.mc.lib.math.Vec

class LinearPath(
    private val start: Vec,
    private val end: Vec,
): Path() {
    override fun getPositionAt(t: Double): Vec =
        start.lerp(end, t)
}

