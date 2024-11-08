package nl.xandermarc.mc.lib.commands.annotations

/**
 * This annotation indicates that the annotated parameter should consume all remaining input.
 *
 * @property separator The string used to join values when consuming input. Defaults to a single space.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Greedy(val separator: String = " ")
