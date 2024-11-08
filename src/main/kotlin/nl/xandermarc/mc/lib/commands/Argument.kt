package nl.xandermarc.mc.lib.commands

import kotlin.reflect.KClass
import kotlin.reflect.KParameter

abstract class Argument<T : Any>(val type: KClass<T>) {
    abstract fun canParseArgument(args: List<String>, parameter: KParameter): Boolean
    abstract fun parseArgument(args: List<String>, parameter: KParameter): Pair<T, Int>
}
