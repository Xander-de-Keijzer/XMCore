package nl.xandermarc.mc.lib.commands.annotations

/**
 * This annotation will limit a command or subcommand with a permission, a no permission message must be provided.
 *
 * @property message The message to display if the user has no permission.
 * @property permission The permission to limit this (sub)command by.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Permission(val message: String, val permission: String="")
