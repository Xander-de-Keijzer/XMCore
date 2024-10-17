package nl.xandermarc.mc.lib.math

import nl.xandermarc.mc.XMCPlugin
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.Vector
import org.joml.Vector3d
import org.joml.Vector3f

fun Double.toRadians() = Math.toRadians(this)
fun Double.toDegrees() = Math.toDegrees(this)

fun Float.toRadians() = Math.toRadians(this.toDouble()).toFloat()
fun Float.toDegrees() = Math.toDegrees(this.toDouble()).toFloat()

fun Double.squared() = this * this
fun Double.cubed() = this * this * this

fun Vector3f.toBukkit() = Vector.fromJOML(this)
fun Vector3d.toBukkit() = Vector.fromJOML(this)

fun Vector3f.toLocation(world: World? = null) =
    Location(world ?: XMCPlugin.world, x.toDouble(), y.toDouble(), z.toDouble())

fun Vector3d.toLocation(world: World? = null) = Location(world ?: XMCPlugin.world, x, y, z)

fun Vector3f.toLocation(world: String) = toLocation(XMCPlugin.server.getWorld(world))
fun Vector3d.toLocation(world: String) = toLocation(XMCPlugin.server.getWorld(world))

fun Location.toVector3f() = Vector3f(x.toFloat(), y.toFloat(), z.toFloat())
fun Location.toVector3d() = Vector3d(x, y, z)

fun Vector3f.toVector3d() = Vector3d(Vector3f(this))
fun Vector3d.toVector3f() = Vector3f(this.x.toFloat(), this.y.toFloat(), this.z.toFloat())

fun evaluate(p0: Vector3d, p1: Vector3d, p2: Vector3d, p3: Vector3d, t: Double): Vector3d {
    val u = 1.0 - t
    val tt = t * t
    val uu = u * u
    val uuu = uu * u
    val ttt = tt * t

    val term0 = Vector3d(p0).mul(uuu)                     // (1 - t)^3 * P0
    val term1 = Vector3d(p1).mul(3.0 * uu * t)            // 3 * (1 - t)^2 * t * P1
    val term2 = Vector3d(p2).mul(3.0 * u * tt)            // 3 * (1 - t) * t^2 * P2
    val term3 = Vector3d(p3).mul(ttt)                     // t^3 * P3

    return term0.add(term1).add(term2).add(term3)
}

fun derivative(p0: Vector3d, p1: Vector3d, p2: Vector3d, p3: Vector3d, t: Double): Vector3d {
    val u = 1.0 - t
    val tt = t * t
    val uu = u * u

    val term0 = Vector3d(p1).sub(p0).mul(3.0 * uu)        // 3 * (1 - t)^2 * (P1 - P0)
    val term1 = Vector3d(p2).sub(p1).mul(6.0 * u * t)     // 6 * (1 - t) * t * (P2 - P1)
    val term2 = Vector3d(p3).sub(p2).mul(3.0 * tt)        // 3 * t^2 * (P3 - P2)

    return term0.add(term1).add(term2)
}
