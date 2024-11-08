package nl.xandermarc.mc.lib.commands.annotations

/**
 * This annotation indicates that the annotated parameter should be converted to lowercase, this annotation will only work for the [String] type.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Lower
