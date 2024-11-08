package nl.xandermarc.test

import nl.xandermarc.mc.lib.commands.Command
import nl.xandermarc.mc.lib.commands.annotations.Greedy
import nl.xandermarc.mc.lib.data.Globals
import kotlin.reflect.KClass
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberFunctions

object Tests {
    fun commandArguments(command: Command) {
        command::class.memberFunctions.filter { it.parameters.size > 1 }.forEach { func ->
            if (func.parameters.dropLast(1).any {
                    it.type.classifier == List::class || it.hasAnnotation<Greedy>()
                }) {
                Globals.logger.warning("Function '${func.name}(${
                    func.parameters.drop(1).joinToString(", ") { "${it.name}: ${(it.type.classifier as KClass<*>).simpleName}" }
                })' of command '${command::class.simpleName}' has an argument of type list or greedy string\n" +
                        "which will take all remaining arguments from the stack, but has more arguments defined after making the function unsatisfiable.")
            }
        }
    }
}
