package nl.xandermarc.mc.lib.commands.annotations

/**
 * This annotation marks a function (subcommand) to only be executed if the command was executed by a player.
 */
@Target(AnnotationTarget.FUNCTION)
annotation class PlayerOnly
