package nl.xandermarc.mc.lib.commands.arguments

import nl.xandermarc.mc.lib.commands.Argument
import kotlin.reflect.KClass
import kotlin.reflect.KParameter

@Suppress("UNCHECKED_CAST")
object EnumArgument : Argument<Enum<*>>(Enum::class) {

    override fun canParseArgument(args: List<String>, parameter: KParameter): Boolean {
        val enum = parameter.type.classifier as? KClass<Enum<*>> ?: return false
        val enums = (enum.members
            .firstOrNull { it.name == "values" } ?: return false)
            .call() as Array<Enum<*>>
        return enums.any { it.name.equals(args.first(), ignoreCase = true) }
    }

    override fun parseArgument(args: List<String>, parameter: KParameter): Pair<Enum<*>, Int> =
        Pair(((parameter.type.classifier as KClass<Enum<*>>)
            .members.first { it.name == "values" }
            .call() as Array<Enum<*>>
                ).first { it.name.equals(args.first(), ignoreCase = true) }
            , 1)
}
