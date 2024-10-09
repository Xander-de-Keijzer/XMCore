package nl.xandermarc.mc.lib.math

import kotlin.math.sqrt

data class Vec(
    val x: Double = 0.0,
    val y: Double = 0.0,
    val z: Double = 0.0
) {

    // Operator Overloads
    /**
     * Adds two vectors component-wise.
     *
     * @param other The vector to add.
     * @return A new Vec instance representing the sum.
     */
    operator fun plus(other: Vec): Vec = Vec(x + other.x, y + other.y, z + other.z)

    /**
     * Subtracts two vectors component-wise.
     *
     * @param other The vector to subtract.
     * @return A new Vec instance representing the difference.
     */
    operator fun minus(other: Vec): Vec = Vec(x - other.x, y - other.y, z - other.z)

    /**
     * Multiplies the vector by a scalar.
     *
     * @param scalar The scalar value to multiply by.
     * @return A new Vec instance representing the scaled vector.
     */
    operator fun times(scalar: Double): Vec = Vec(x * scalar, y * scalar, z * scalar)

    // Distance Functions

    /**
     * Calculates the Euclidean distance between this vector and another vector.
     *
     * @param other The other vector.
     * @return The distance between the two vectors.
     */
    fun distanceTo(other: Vec): Double {
        val dx = this.x - other.x
        val dy = this.y - other.y
        val dz = this.z - other.z
        return kotlin.math.sqrt(dx * dx + dy * dy + dz * dz)
    }

    /**
     * Calculates the squared Euclidean distance between this vector and another vector.
     *
     * @param other The other vector.
     * @return The squared distance between the two vectors.
     */
    fun distanceSquaredTo(other: Vec): Double {
        val dx = this.x - other.x
        val dy = this.y - other.y
        val dz = this.z - other.z
        return dx * dx + dy * dy + dz * dz
    }

    // Lerp Function

    /**
     * Linearly interpolates between this vector and the [target] vector by the factor [t].
     *
     * @param target The target vector to interpolate towards.
     * @param t The interpolation factor between 0.0 and 1.0.
     * @return A new Vec instance representing the interpolated vector.
     */
    fun lerp(target: Vec, t: Double): Vec {
        val clampedT = t.coerceIn(0.0, 1.0)
        return this + (target - this) * clampedT
    }

    // Additional Utility Functions

    /**
     * Calculates the dot product of this vector with another vector.
     *
     * @param other The other vector.
     * @return The dot product as a Double.
     */
    fun dot(other: Vec): Double = x * other.x + y * other.y + z * other.z

    /**
     * Calculates the cross product of this vector with another vector.
     *
     * @param other The other vector.
     * @return A new Vec instance representing the cross product.
     */
    fun cross(other: Vec): Vec = Vec(
        y * other.z - z * other.y,
        z * other.x - x * other.z,
        x * other.y - y * other.x
    )

    val lengthSquared: Double
        get() = x*x+y*y+z*z

    val length: Double
        get() = sqrt(lengthSquared)

    /**
     * Normalizes the vector to have a magnitude of 1.
     *
     * @return A new Vec instance representing the normalized vector.
     *         If the vector is zero-length, returns the original vector.
     */
    fun normalize(): Vec {
        val mag = length
        return if (mag != 0.0) {
            this * (1.0 / mag)
        } else {
            this
        }
    }
}