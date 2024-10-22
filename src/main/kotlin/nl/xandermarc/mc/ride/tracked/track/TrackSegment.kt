package nl.xandermarc.mc.ride.tracked.track

import kotlinx.serialization.Serializable
import nl.xandermarc.mc.lib.path.BezierPath
import nl.xandermarc.mc.lib.serializers.QuaterniondSerializer
import nl.xandermarc.mc.lib.serializers.Vector3dSerializer
import org.joml.Quaterniond
import org.joml.Vector3d
import kotlin.math.floor

@Serializable
data class TrackSegment(
    val path: BezierPath,
    val defaultA: Int? = null,
    val defaultB: Int? = null,
    var aConnected: Int? = defaultA,
    var bConnected: Int? = defaultB,
    @Serializable(with = QuaterniondSerializer::class)
    var rotation: Quaterniond = Quaterniond(),
    @Serializable(with = Vector3dSerializer::class)
    var translation: Vector3d = Vector3d(),
    @Serializable(with = Vector3dSerializer::class)
    var rotationPoint: Vector3d = Vector3d(),

    private val tValues: MutableList<Double> = mutableListOf()
) {
    var totalArcLength = 0.0

    private fun positionAt(t: Double): Vector3d {
        val position = path.positionAt(t)
        val translatedPosition = Vector3d(position).sub(rotationPoint)
        val rotatedPosition = rotation.transform(translatedPosition)
        val finalPosition = rotatedPosition.add(rotationPoint).add(translation)

        return finalPosition
    }
    private fun forwardAt(t: Double): Vector3d {
        val forward = path.forwardAt(t)
        return rotation.transform(forward)
    }
    fun positionAtDistance(distance: Double): Vector3d {
        return positionAt(distanceToT(distance))
    }
    fun forwardAtDistance(distance: Double): Vector3d {
        return forwardAt(distanceToT(distance))
    }

    fun generateTValues(
        steps: Int = (path.length() * 10).toInt().coerceAtLeast(100),
        step: Double = 0.01
    ) {
        tValues.clear()

        // Precompute positions and cumulative arc lengths
        val n = steps.coerceAtLeast(1)
        val tList = (0..n).map { it.toDouble() / n }
        val positions = tList.map { path.positionAt(it) }  // Positions before transformation
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

}

/*
Previous Position
Current Position
Target Position
Progression: Double
Duration: Int

TransferState {
    LOCKED,
    POSITIONED,
    MOVING,
    CHANGING,
    STOPPING,
    STOPPED,
    EMERGENCY
}

function easeInOutSine(x) {
    return -(Math.cos(Math.PI * x) - 1) / 2;
}

function easeOutSine(x) {
    return Math.sin((x * Math.PI) / 2);
}

https://nicmulvaney.com/easing#easeInOutBounce

// Option
Ease in > Linear > Ease out
Ease in for X ticks
Linear else
Ease out for X ticks til end

if STOP Ease out immediately
after ease out set previous to current and target to new target

tick() {
    moving = Current != Target
    if moving {
        increment = 1 / Duration.toDouble() # Inc per tick
        Progression += increment
        if Progression >= 1.0 {
            Current = Target
        } else {
            Current = Previous.lerp(Target, easeInOutSine(Progression))
        }
    }
}

move(target, duration) {
    Previous = Current
    Target = target
    Duration = duration
    Progression = 0.0
}

 */
