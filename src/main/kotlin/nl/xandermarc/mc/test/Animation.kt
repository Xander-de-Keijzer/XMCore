package nl.xandermarc.mc.test

import nl.xandermarc.mc.lib.data.Globals
import org.joml.Vector3d
import kotlin.math.pow

class Animation(
    private var start: Vector3d,
    private var end: Vector3d,
    private val duration: Int,
    private val easeInTicks: Int,
    private val easeOutTicks: Int
) {
    private var progression: Int = 0
    private var current: Vector3d = Vector3d(start)
    private var pendingEnd: Vector3d? = null
    private var easingOut = false
    private var easingIn = true // Start with easing in initially
    private var easeOutStartPoint: Vector3d = Vector3d(start)
    private var easeOutProgression: Int = 0
    private var easeOutTargetProgression: Double = 0.0

    // Easing function for ease-out using Double
    private fun easeOut(t: Double): Double {
        return 1 - (1 - t).pow(3.0) // Smooth ease-out towards stopping
    }

    // Easing function for ease-in using Double
    private fun easeIn(t: Double): Double {
        return t.pow(3.0) // Smooth acceleration for ease-in
    }

    // Function to update the animation state on each tick
    fun tick() {
        if (easingOut) {
            // Handling the ease-out phase from the current progression onwards, targeting a stop point
            if (easeOutProgression < easeOutTicks) {
                val t = easeOutProgression / easeOutTicks.toDouble()
                val easeFactor = easeOut(t)
                current = easeOutStartPoint.lerp(start.lerp(end, easeOutTargetProgression, Vector3d()), easeFactor, Vector3d())
                easeOutProgression++
            } else {
                // Ease-out complete, prepare for the new animation phase
                easingOut = false
                easingIn = true // Set to easing in for a smooth acceleration
                start.set(current) // Start the new animation from the current position after ease-out
                if (pendingEnd != null) {
                    end.set(pendingEnd)
                    pendingEnd = null
                }
                progression = 0 // Reset progression for the new animation
            }
        } else if (easingIn) {
            // Ease-in phase for the new animation
            if (progression < easeInTicks) {
                val t = progression / easeInTicks.toDouble()
                val easeFactor = easeIn(t)
                current = start.lerp(end, easeFactor, Vector3d())
                progression++
            } else {
                easingIn = false // Finish easing in
                progression = easeInTicks // Set progression to continue smoothly
            }
        } else {
            // Normal animation phase after ease-in
            if (progression < duration) {
                val t = (progression - easeInTicks).toDouble() / (duration - easeInTicks).toDouble()
                current = start.lerp(end, t.coerceIn(0.0, 1.0), Vector3d())
                progression++
            }
        }
        logCurrentState()
    }

    // Function to change the end point
    fun changeEndPoint(newEnd: Vector3d) {
        if (!easingOut) {
            // Start a new ease-out if not already easing out
            pendingEnd = Vector3d(newEnd) // Store the new end point to be applied after ease-out completes
            easingOut = true
            easeOutStartPoint.set(current) // Start easing out from the current point
            easeOutProgression = 0 // Start the ease-out phase from the current point

            // Determine where to stop during the ease-out phase
            val currentProgressFraction = progression / duration.toDouble()
            easeOutTargetProgression = (currentProgressFraction + (easeOutTicks.toDouble() / duration)).coerceAtMost(1.0)
        } else {
            // If already easing out, update the pending end
            pendingEnd = Vector3d(newEnd)
        }
    }

    // Get the current position of the animation
    fun getCurrent(): Vector3d {
        return current
    }

    // Optional utility for debugging or logging purposes
    fun logCurrentState() {
        Globals.logger.info("[Animation] Current Position: $current | Progression: $progression")
    }
}

fun main() {
    val animation = Animation(Vector3d(0.0), Vector3d(10.0), 100, 10, 10)
    for (i in 0..50) {
        animation.tick()
    }
    animation.changeEndPoint(Vector3d(-10.0))
    for (i in 0..100) {
        animation.tick()
    }
}
