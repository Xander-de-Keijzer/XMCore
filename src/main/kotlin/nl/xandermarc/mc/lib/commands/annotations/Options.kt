package nl.xandermarc.mc.lib.commands.annotations

/**
 * This annotation indicates the allowed options for this parameter, this annotation will only work for the [String] type.
 *
 * @property options The allowed options for this parameter.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Options(vararg val options: String)
