package nl.xandermarc.mc.lib.commands.annotations

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Options(vararg val options: String)
