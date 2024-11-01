package nl.xandermarc.mc.lib.commands.annotations

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Range(val min: Int = 0, val max: Int = Int.MAX_VALUE)
