package nl.xandermarc.mc.lib.math

import nl.xandermarc.mc.lib.math.extensions.cubed
import nl.xandermarc.mc.lib.math.extensions.squared
import org.joml.Quaternionf
import org.joml.Vector3f

class CubicBezier(
    val p1: Vec,
    val p2: Vec,
    val p3: Vec,
) {
    /**
     * Evaluates the cubic Bezier curve at a given parameter t (0 <= t <= 1).
     */
    fun evaluate(t: Double): Vec {
        val u = 1 - t

        return p1 * 3.0 * u.squared() * t +
                p2 * 3.0 * u * t.squared() +
                p3 * 1.0 * t.cubed()
    }

    /**
     * Calculates the derivative of the cubic Bezier curve at a given parameter t (0 <= t <= 1).
     */
    fun derivative(t: Double): Vec {
        val u = 1 - t

        return p1 * 3.0 * u.squared() +
                p2 * 6.0 * u * t +
                p3 * 3.0 * t.squared()
    }

    /**
     * Calculates the arc length of the cubic Bezier curve using numerical integration.
     * This uses a simple trapezoidal approximation.
     */
    fun arcLength(steps: Int = 1000): Double {
        var length = 0.0
        var previousPoint = evaluate(0.0)
        for (i in 1..steps) {
            val t = i.toDouble() / steps
            val currentPoint = evaluate(t)
            length += previousPoint.distanceTo(currentPoint)
            previousPoint = currentPoint
        }
        return length
    }

    /**
     * Finds the parameter t that corresponds to a specific arc length along the curve.
     * Uses binary search to approximate the value of t.
     */
    fun findTForArcLength(targetLength: Double, steps: Int = 1000, epsilon: Double = 1e-5): Double {
        var low = 0.0
        var high = 1.0
        var mid: Double

        while (high - low > epsilon) {
            mid = (low + high) / 2.0
            val length = arcLengthUntil(mid, steps)
            if (length < targetLength) {
                low = mid
            } else {
                high = mid
            }
        }
        return (low + high) / 2.0
    }

    /**
     * Calculates the arc length from t = 0 to a given t.
     */
    fun arcLengthUntil(t: Double, steps: Int = 1000): Double {
        var length = 0.0
        var previousPoint = evaluate(0.0)
        for (i in 1..steps) {
            val currentT = i.toDouble() / steps * t
            val currentPoint = evaluate(currentT)
            length += previousPoint.distanceTo(currentPoint)
            previousPoint = currentPoint
        }
        return length
    }

    /**
     * Returns a point on the curve at a specific distance from the start.
     */
    fun pointAtDistance(distance: Double, steps: Int = 1000): Vec {
        val t = findTForArcLength(distance, steps)
        return evaluate(t)
    }

    /**
     * Returns the rotation at a specific point on the curve using the derivative.
     * Uses JOML's Quaternionf to create a rotation that aligns with the tangent of the curve.
     */
    fun rotationAt(t: Double): Quaternionf {
        val tangent = derivative(t)
        val tangentVec3f = Vector3f(tangent.x.toFloat(), tangent.y.toFloat(), tangent.z.toFloat()).normalize()

        // Assuming "up" is along the Y-axis in Minecraft (0, 1, 0)
        val up = Vector3f(0f, 1f, 0f)
        val rotation = Quaternionf().lookAlong(tangentVec3f, up)

        return rotation
    }
}