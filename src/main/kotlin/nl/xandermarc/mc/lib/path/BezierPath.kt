package nl.xandermarc.mc.lib.path

import kotlinx.serialization.Serializable
import nl.xandermarc.mc.lib.serializers.QuaterniondSerializer
import nl.xandermarc.mc.lib.serializers.Vector3dSerializer
import org.joml.Quaterniond
import org.joml.Vector3d
import kotlin.math.floor

@Serializable
data class BezierPath(
    @Serializable(with = Vector3dSerializer::class)
    val p0: Vector3d = Vector3d(),  // Starting point
    @Serializable(with = Vector3dSerializer::class)
    val p1: Vector3d = Vector3d(),
    @Serializable(with = Vector3dSerializer::class)
    val p2: Vector3d = Vector3d(),
    @Serializable(with = Vector3dSerializer::class)
    val p3: Vector3d = Vector3d(),
    @Serializable(with = QuaterniondSerializer::class)
    var rotation: Quaterniond = Quaterniond(),
    @Serializable(with = Vector3dSerializer::class)
    var translation: Vector3d = Vector3d(),
    @Serializable(with = Vector3dSerializer::class)
    var rotationPoint: Vector3d = Vector3d(),  // Point around which to rotate

    private val tValues: MutableList<Double> = mutableListOf()
) : Path() {

    val arcLength: Double
        get() = totalArcLength

    val isEmpty: Boolean
        get() = tValues.isEmpty()

    // Cache the total arc length
    private var totalArcLength: Double = 0.0

    override fun getPositionAt(t: Double): Vector3d {
        val position = evaluate(p0, p1, p2, p3, t)

        // Rotate around the rotationPoint
        val translatedPosition = Vector3d(position).sub(rotationPoint)
        val rotatedPosition = rotation.transform(translatedPosition)
        val finalPosition = rotatedPosition.add(rotationPoint).add(translation)

        return finalPosition
    }

    override fun getRotationAt(t: Double): Quaterniond {
        val dir = derivative(p0, p1, p2, p3, t).normalize()

        // Rotate the direction vector around the rotationPoint
        val rotatedDir = rotation.transform(dir)

        // Combine rotations by multiplying quaternions
        // The current rotation is applied first, then the rotation to look along the direction
        val currentRotation = Quaterniond().lookAlong(rotatedDir, Vector3d(0.0, 1.0, 0.0))

        return currentRotation
    }

    fun generateTValues(
        steps: Int = (approximateCurveLength() * 10).toInt().coerceAtLeast(100),
        step: Double = 0.01
    ) {
        tValues.clear()

        // Precompute positions and cumulative arc lengths
        val n = steps.coerceAtLeast(1)
        val tList = (0..n).map { it.toDouble() / n }
        val positions = tList.map { evaluate(p0, p1, p2, p3, it) }  // Positions before transformation
        val distances = positions.zipWithNext().map { (p1, p2) -> p1.distance(p2) }
        val cumulativeDistances = mutableListOf(0.0)
        distances.forEach { cumulativeDistances.add(cumulativeDistances.last() + it) }
        totalArcLength = cumulativeDistances.last()

        // Handle case where totalLength is zero (degenerate curve)
        if (totalArcLength == 0.0) {
            tValues.add(0.0)
            return
        }

        // Map desired distances to t values
        val numSteps = (totalArcLength / step).toInt().coerceAtLeast(1)
        val stepLength = totalArcLength / numSteps
        for (i in 0..numSteps) {
            val distanceAlongCurve = i * stepLength
            // Use binary search to find the insertion point
            val index = cumulativeDistances.binarySearch(distanceAlongCurve)
            val t: Double
            if (index >= 0) {
                // Exact match found
                t = tList[index]
            } else {
                // Interpolate between the two nearest points
                val insertionPoint = -index - 1
                if (insertionPoint >= cumulativeDistances.size) {
                    t = tList.last()
                } else if (insertionPoint == 0) {
                    t = tList.first()
                } else {
                    val d1 = cumulativeDistances[insertionPoint - 1]
                    val d2 = cumulativeDistances[insertionPoint]
                    val t1 = tList[insertionPoint - 1]
                    val t2 = tList[insertionPoint]
                    val fraction = (distanceAlongCurve - d1) / (d2 - d1)
                    t = t1 + fraction * (t2 - t1)
                }
            }
            tValues.add(t)
        }
    }

    private fun distanceToT(distance: Double): Double {
        // Map a distance along the curve to a parameter t
        val clampedDistance = distance.coerceIn(0.0, totalArcLength)
        val scaledDistance = clampedDistance / totalArcLength * (tValues.size - 1)
        val lowerIndex = floor(scaledDistance).toInt().coerceAtMost(tValues.size - 1)
        val upperIndex = (lowerIndex + 1).coerceAtMost(tValues.size - 1)

        val tLower = tValues[lowerIndex]
        val tUpper = tValues[upperIndex]

        val fraction = scaledDistance - lowerIndex
        return tLower + fraction * (tUpper - tLower)
    }

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

    fun positionAtDistance(distance: Double): Vector3d {
        return getPositionAt(distanceToT(distance))
    }

    fun rotationAtDistance(distance: Double): Quaterniond {
        return getRotationAt(distanceToT(distance))
    }

    // Approximate the curve length by summing distances between control points
    private fun approximateCurveLength(): Double {
        return p0.distance(p1) + p1.distance(p2) + p2.distance(p3)
    }
}
