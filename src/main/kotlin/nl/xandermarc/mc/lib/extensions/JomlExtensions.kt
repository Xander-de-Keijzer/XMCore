package nl.xandermarc.mc.lib.extensions

import nl.xandermarc.mc.core.XMC
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.Vector
import org.joml.Math.toRadians
import org.joml.Quaterniond
import org.joml.Quaternionf
import org.joml.Vector3d
import org.joml.Vector3f
import kotlin.math.cos
import kotlin.math.sin

fun Vector3d.toBukkit() = Vector(x, y, z)
fun Vector3f.toBukkit() = Vector(x, y, z)

fun Vector3d.toLocation(world: World? = null) = Location(world ?: XMC.world, x, y, z)
fun Vector3f.toLocation(world: World? = null) = toVector3d().toLocation(world)

fun Vector3d.toLocation(world: String) = toLocation(XMC.server.getWorld(world))
fun Vector3f.toLocation(world: String) = toLocation(XMC.server.getWorld(world))

fun Location.toVector3d() = Vector3d(x, y, z)
fun Location.toVector3f() = Vector3f(x.toFloat(), y.toFloat(), z.toFloat())

fun Vector3f.toVector3d() = Vector3d(this)
fun Vector3d.toVector3f() = Vector3f(x.toFloat(), y.toFloat(), z.toFloat())

fun Quaternionf.toQuaterniond() = Quaterniond(this)
fun Quaterniond.toQuaternionf() = Quaternionf(this)

fun Quaterniond.rotateYawPitchRoll(yawDegrees: Double, pitchDegrees: Double, rollDegrees: Double): Quaterniond {
    val hy = toRadians(yawDegrees) * 0.5
    val hp = toRadians(pitchDegrees) * 0.5
    val hr = toRadians(rollDegrees) * 0.5

    val cy = cos(hy)
    val sy = sin(hy)
    val cp = cos(hp)
    val sp = sin(hp)
    val cr = cos(hr)
    val sr = sin(hr)

    w = cy * cp * cr - sy * sp * sr
    x = cy * cp * sr + sy * sp * cr
    y = sy * cp * cr + cy * sp * sr
    z = cy * sp * cr - sy * cp * sr
    return this
}
fun Quaternionf.rotateYawPitchRoll(yawDegrees: Double, pitchDegrees: Double, rollDegrees: Double): Quaternionf =
    toQuaterniond().rotateYawPitchRoll(yawDegrees, pitchDegrees, rollDegrees).toQuaternionf()
