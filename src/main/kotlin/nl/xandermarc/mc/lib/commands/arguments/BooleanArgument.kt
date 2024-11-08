package nl.xandermarc.mc.lib.commands.arguments

import nl.xandermarc.mc.lib.commands.Argument
import kotlin.reflect.KParameter

object BooleanArgument : Argument<Boolean>(Boolean::class) {
    override fun canParseArgument(args: List<String>, parameter: KParameter): Boolean {
        return args.first().lowercase() in listOf("yes", "no", "true", "false", "--${parameter.name?.lowercase()}")
    }

    override fun parseArgument(args: List<String>, parameter: KParameter): Pair<Boolean, Int> {
        return Pair(args.first().lowercase() in listOf("yes", "true", "--${parameter.name?.lowercase()}"), 1)
    }
}
