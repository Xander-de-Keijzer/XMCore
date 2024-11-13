package nl.xandermarc.mc.lib.commands

import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import nl.xandermarc.mc.lib.commands.annotations.*
import nl.xandermarc.mc.lib.commands.arguments.*
import nl.xandermarc.mc.lib.extensions.nameSuffix
import nl.xandermarc.mc.lib.extensions.subclassOf
import nl.xandermarc.mc.lib.interfaces.Executor
import nl.xandermarc.test.Tests
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import kotlin.reflect.*
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberFunctions


/**
 * Represents a command within the Minecraft plugin framework.
 *
 * @property name The name of the command. Defaults to the suffix of the class name if not provided.
 * @property description A brief description of the command's functionality.
 *
 * @constructor Creates a Command instance with an optional name and description.
 *
 * The Command class also integrates with Paper's lifecycle events to automatically register commands when
 * the plugin lifecycle reaches the appropriate stage.
 *
 * To create a command, extend the Command class and define functions within the subclass. Each function
 * represents a different command that can be executed. The arguments of the function are automatically parsed
 * from the command input if possible. For example, if you define a function like:
 *
 * ```kotlin
 * fun create(track: String) {
 *     // Command logic here
 * }
 * ```
 *
 * The `track` argument will be parsed from the input provided by the player, assuming a string input is available.
 * If the command is `/command create myTrack`, the `track` parameter will be assigned the value "myTrack".
 *
 * The supported types are [Boolean], [Enum], [Int], [List]<[String]>, [org.bukkit.entity.Player], [String].
 * There is a special type [Unit] which can be used to create a literal argument, meaning the player has to pass the
 * parameter name as an argument. For example `fun players(list: Unit)` will require `/<cmd> players list`.
 *
 * Annotations can be used to modify the behavior of the command:
 * - **@[Greedy]**: Captures all remaining arguments as a single parameter, joined by the specified separator. For instance, `@Greedy("_") track: String` will capture everything after the command keyword, allowing for spaces in the value, and join them using "_" as the separator.
 * - **@[Lower]**: Converts the argument value to lowercase, ensuring that comparisons are case-insensitive.
 * - **@[Permission]**: Restricts command execution to users with the specified permission node. This helps enforce role-based access control for commands.
 * - **@[PlayerOnly]**: Ensures that the command can only be executed by a player (not from the console or other sources). This is useful when the command logic involves player-specific actions.
 * - **@[Options]**: Restricts the argument to a predefined set of valid options, ensuring that only specific values are allowed.
 * - **@[Root]**: Marks the function as the root command, which means the name of the function will be ignored and thus not used as a subcommand.
 * - **@[Range]**: Defines a valid numeric range for the argument. This is useful for ensuring that input values fall within a specific range, such as limiting a value between 1 and 100.
 */
@Suppress("UnstableApiUsage", "MemberVisibilityCanBePrivate")
abstract class Command(
    val name: String? = null,
    val description: String? = null,
) : BasicCommand, Executor {
    protected lateinit var source: CommandSender
    protected var executor: Entity? = null
    protected val player: Player
        get() = source as? Player ?: throw IllegalStateException(
            "accessing player is only safe when function is marked with @PlayerOnly"
        )
    private val commandName get() = name ?: this::class.nameSuffix("command")

    override fun register(plugin: JavaPlugin): Executor {
        plugin.lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) {
            it.registrar().register(commandName, description, this)
        }

        Tests.commandArguments(this)
        return this
    }

    private fun executeCommandOnMemberFunction(arguments: List<String>, memberFunction: KFunction<*>): Any? {
        if (memberFunction.hasAnnotation<PlayerOnly>() && source !is Player) return null
        if (arguments.isEmpty() && memberFunction.parameters.size == 1) return memberFunction.call(this)

        var args = arguments
        val build = hashMapOf<KParameter, Any>(memberFunction.parameters.first() to this)

        for (parameter in memberFunction.parameters.drop(1)) {
            if (args.isEmpty()) return null

            val argumentType = argumentTypes.firstOrNull {
                parameter.type.classifier.subclassOf(it.type) &&
                it.canParseArgument(args, parameter)
            } ?: if (parameter.isOptional) continue else return null

            argumentType.parseArgument(args, parameter).apply {
                build[parameter] = first
                args = args.drop(second)
            }
        }
        if (args.isEmpty()) return memberFunction.callBy(build)
        return null
    }

    private fun noPermission(commandSourceStack: CommandSourceStack, message: String) {
        commandSourceStack.sender.sendMessage(message)
    }

    override fun execute(commandSourceStack: CommandSourceStack, args: Array<out String>) {
        this::class.findAnnotation<Permission>()?.let {
            val permission = if (it.permission != "") it.permission else "command.$commandName"
            if (!commandSourceStack.sender.hasPermission(permission)) return noPermission(commandSourceStack, it.message)
        }
        source = commandSourceStack.sender
        executor = commandSourceStack.executor
        for (memberFunction in this::class.memberFunctions) {
            memberFunction.findAnnotation<Permission>()?.let {
                val permission = if (it.permission != "") it.permission else "command.$commandName.${memberFunction.name}"
                if (!commandSourceStack.sender.hasPermission(permission)) return noPermission(commandSourceStack, it.message)
            }

            if (memberFunction.visibility != KVisibility.PUBLIC) continue
            if (memberFunction.name in listOf("canUse", "equals", "execute", "hashCode", "permission", "register", "suggest", "toString")) continue
            memberFunction.findAnnotation<Root>()?.let {
                executeCommandOnMemberFunction(args.toMutableList(), memberFunction)
            }
            if (args.isNotEmpty() && args.first() == memberFunction.name) {
                executeCommandOnMemberFunction(
                    args.drop(1).toList(),
                    memberFunction
                )
            }
        }
    }

    companion object {
        val argumentTypes = mutableListOf(
            LiteralArgument,
            StringArgument,
            IntegerArgument,
            PlayerArgument,
            ListArgument,
            EnumArgument,
            BooleanArgument
        )
        fun type(kClassifier: KClassifier?) = typeNull(kClassifier) ?: throw IllegalArgumentException("No argument type for type $kClassifier exists.")
        fun typeNull(kClassifier: KClassifier?): Argument<*>? = argumentTypes.firstOrNull { it.type == kClassifier }
        fun type(kClass: KClass<*>?): Argument<*>? = argumentTypes.firstOrNull { it.type == kClass }
    }
}
