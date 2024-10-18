package nl.xandermarc.mc.lib.path

import org.joml.Quaterniond
import org.joml.Vector3d

class MultiPath(
    private vararg val paths: Path
) : Path() {
    override fun getPositionAt(t: Double): Vector3d {
        val totalT = (t * paths.size)
        return paths[totalT.toInt()].positionAt(totalT % 1)
    }

    override fun getRotationAt(t: Double): Quaterniond {
        val totalT = (t * paths.size)
        return paths[totalT.toInt()].rotationAt(totalT % 1)
    }
}
