package nl.xandermarc.mc.lib.commands.arguments

import nl.xandermarc.mc.lib.commands.Argument
import nl.xandermarc.mc.lib.commands.annotations.Lower
import kotlin.reflect.KParameter
import kotlin.reflect.full.hasAnnotation

object ListArgument : Argument<List<*>>(List::class) {
    override fun canParseArgument(args: List<String>, parameter: KParameter) = true
    override fun parseArgument(args: List<String>, parameter: KParameter): Pair<List<*>, Int> =
        if (parameter.hasAnnotation<Lower>()) Pair(args.map { it.lowercase() }, args.size)
        else Pair(args, args.size)
}
