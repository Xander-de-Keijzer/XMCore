package nl.xandermarc.mc.lib.math.path

import nl.xandermarc.mc.lib.math.Vec
import nl.xandermarc.mc.lib.math.evaluate

class BezierPath(
    private val p0: Vec = Vec(),
    private val p1: Vec = Vec(),
    private val p2: Vec = Vec(),
    private val p3: Vec = Vec()
): Path() {
    constructor(p1: Vec, p2: Vec, p3: Vec): this(p0=Vec(), p1=p1, p2=p2, p3=p3)
    override fun getPositionAt(t: Double): Vec =
        if (p0.length == 0.0) evaluate(p1, p2, p3, t)
        else evaluate(p0, p1, p2, p3, t)
}
