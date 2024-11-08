package nl.xandermarc.mc.lib.commands.annotations

import nl.xandermarc.mc.lib.data.Ranks

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class RankPermission(val rank: Ranks, val message: String = "You don't have permission for this command.")
