package nl.xandermarc.mc.lib.commands.annotations

/**
 * This annotation limits an [Int] parameter within a range.
 *
 * @property min The minimum value of the [Int], defaults to 0
 * @property max The maximum value of the [Int], defaults to [Int.MAX_VALUE]
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Range(val min: Int = 0, val max: Int = Int.MAX_VALUE)
