package nl.xandermarc.mc.lib.math.path

import nl.xandermarc.mc.lib.math.Vec

class MultiPath(
    private vararg val paths: Path
): Path() {
    override fun getPositionAt(t: Double): Vec {
        val totalT = (t * paths.size)
        return paths[totalT.toInt()].positionAt(totalT % 1)
    }
}
