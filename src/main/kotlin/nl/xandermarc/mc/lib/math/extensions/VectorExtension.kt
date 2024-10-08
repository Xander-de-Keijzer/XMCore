package nl.xandermarc.mc.lib.math.extensions

import nl.xandermarc.mc.Main
import nl.xandermarc.mc.lib.math.Vec
import org.bukkit.Location
import org.bukkit.util.Vector
import org.joml.Vector3f

fun Vector.toVec(): Vec {
    return Vec(x, y, z)
}

fun Vector3f.toVec(): Vec {
    return Vec(x.toDouble(), y.toDouble(), z.toDouble())
}

fun Vec.toVector(): Vector {
    return Vector(x, y, z)
}

fun Vec.toVector3f(): Vector3f {
    return Vector3f(x.toFloat(), y.toFloat(), z.toFloat())
}

fun Location.vecTo(other: Location): Vec {
    return Vec(other.x - x, other.y - y, other.z - z)
}

fun Location.toVec(): Vec {
    return Vec(x, y, z)
}

fun Vec.toLocation(): Location {
    return Location(Main.world, x, y, z)
}
