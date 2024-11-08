package nl.xandermarc.mc.lib.commands.arguments

import nl.xandermarc.mc.lib.commands.Argument
import kotlin.reflect.KParameter

object LiteralArgument : Argument<Unit>(Unit::class) {
    override fun canParseArgument(args: List<String>, parameter: KParameter): Boolean = args.first() == parameter.name
    override fun parseArgument(args: List<String>, parameter: KParameter): Pair<Unit, Int> = Pair(Unit, 1)
}
