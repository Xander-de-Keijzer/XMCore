package nl.xandermarc.mc.lib.area

import org.joml.Vector3d

class MultiArea(
    private vararg val areas: Area
) : Area() {
    override val min get() = Vector3d(areas.minOf { it.min.x }, areas.minOf { it.min.y }, areas.minOf { it.min.z })
    override val max get() = Vector3d(areas.maxOf { it.max.x }, areas.maxOf { it.max.y }, areas.maxOf { it.max.z })
    override fun contains(x: Double, y: Double, z: Double) =
        areas.any { it.contains(x, y, z) }
}
