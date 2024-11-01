package nl.xandermarc.mc.lib.area

import org.bukkit.World
import org.joml.Vector2d
import org.joml.Vector3d
import kotlin.math.max
import kotlin.math.min

class CuboidArea(
    var a: Vector3d,
    var b: Vector3d,
    var square: Boolean = false,
    world: World? = null
) : Area(world) {
    override val min get() = Vector3d(min(a.x, b.x), min(a.y, b.y), min(a.z, b.z))
    override val max get() = Vector3d(max(a.x, b.x), max(a.y, b.y), max(a.z, b.z))
    override fun contains(x: Double, y: Double, z: Double): Boolean {
        return x in min.x..max.x
                && (square || y in min.y..max.y)
                && z in min.z..max.z
    }

    override fun contains(vector: Vector2d): Boolean =
        vector.x in min.x..max.x &&
        vector.y in min.z..max.z
}
