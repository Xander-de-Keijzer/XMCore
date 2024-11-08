package nl.xandermarc.mc.lib.commands.arguments

import nl.xandermarc.mc.lib.commands.Argument
import nl.xandermarc.mc.lib.commands.annotations.Greedy
import nl.xandermarc.mc.lib.commands.annotations.Lower
import nl.xandermarc.mc.lib.commands.annotations.Options
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

object StringArgument : Argument<String>(String::class) {
    override fun canParseArgument(args: List<String>, parameter: KParameter): Boolean {
        val arg = if (parameter.hasAnnotation<Greedy>()) args.joinToString(" ")
        else args.firstOrNull()

        parameter.findAnnotation<Options>()?.run {
            if (parameter.hasAnnotation<Lower>()) return arg?.lowercase() in options
            else return arg in options
        }
        return true
    }

    override fun parseArgument(args: List<String>, parameter: KParameter): Pair<String, Int> {
        val arg = parameter.findAnnotation<Greedy>()?.run {
            Pair(args.joinToString(separator), args.size)
        } ?: Pair(args.first(), 1)
        return if(parameter.hasAnnotation<Lower>()) Pair(arg.first.lowercase(), arg.second) else arg
    }
}
