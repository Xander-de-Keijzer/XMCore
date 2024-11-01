package nl.xandermarc.mc.lib.area

import org.joml.Vector2d
import org.joml.Vector3d

class SphereArea(
    var center: Vector3d,
    var radius: Double,
    var cylinder: Boolean = false
) : Area() {
    override val min: Vector3d get() = center.sub(radius, radius, radius)
    override val max: Vector3d get() = center.add(radius, radius, radius)

    override fun contains(x: Double, y: Double, z: Double): Boolean =
        if(cylinder) contains(Vector2d(x, z))
        else center.distanceSquared(x, y, z) <= radius * radius

    override fun contains(vector: Vector2d): Boolean =
        Vector2d(center.x, center.z).distanceSquared(vector.x, vector.y) <= radius * radius

    override val chunkCoords: List<Pair<Int, Int>>
        get() = super.chunkCoords.filter { it in this }

    private operator fun contains(chunk: Pair<Int, Int>): Boolean {
        return  contains(chunk, 0, 0) ||
                contains(chunk, 15, 0) ||
                contains(chunk, 0, 15) ||
                contains(chunk, 15, 15)
    }

    private fun contains(chunk: Pair<Int, Int>, blockX: Int, blockZ: Int): Boolean {
        val x = (chunk.first shl 4) or blockX
        val z = (chunk.second shl 4) or blockZ
        return contains(Vector2d(x.toDouble(), z.toDouble()))
    }
}
