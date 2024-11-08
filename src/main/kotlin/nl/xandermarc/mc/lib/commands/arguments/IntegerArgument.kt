package nl.xandermarc.mc.lib.commands.arguments

import nl.xandermarc.mc.lib.commands.Argument
import nl.xandermarc.mc.lib.commands.annotations.Range
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation

object IntegerArgument : Argument<Int>(Int::class) {
    override fun canParseArgument(args: List<String>, parameter: KParameter): Boolean {
        val i = try { args.first().toInt() }
        catch (e: NumberFormatException) { return false }
        parameter.findAnnotation<Range>()?.run {
            return i in min..max
        }
        return true
    }

    override fun parseArgument(args: List<String>, parameter: KParameter): Pair<Int, Int> {
        return Pair(args.first().toInt(), 1)
    }
}
