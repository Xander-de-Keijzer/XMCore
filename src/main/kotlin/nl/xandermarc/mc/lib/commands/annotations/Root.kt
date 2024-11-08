package nl.xandermarc.mc.lib.commands.annotations

/**
 * This annotation marks a function as being a root command, this means that the function name will be ignored.
 *
 * For example:
 * `fun edit(name: String)` will be executed when running `/<cmd> edit <name>`
 *
 * But when marked with this annotation:
 *
 * `@Root fun edit(name: String)` will be executed when running `/<cmd> <name>`
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Root
