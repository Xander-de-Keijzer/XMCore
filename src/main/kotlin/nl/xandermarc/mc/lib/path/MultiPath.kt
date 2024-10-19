package nl.xandermarc.mc.lib.path

import org.joml.Vector3d

class MultiPath(
    private vararg val paths: Path
) : Path() {
    override fun getPositionAt(t: Double): Vector3d {
        val totalT = (t * paths.size)
        return paths[totalT.toInt()].positionAt(totalT % 1)
    }

    override fun getForwardAt(t: Double): Vector3d {
        val totalT = (t * paths.size)
        return paths[totalT.toInt()].forwardAt(totalT % 1)
    }

    override fun length(): Double = paths.sumOf { it.length() }
}
