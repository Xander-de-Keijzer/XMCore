package nl.xandermarc.mc.lib.commands

import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import nl.xandermarc.mc.lib.commands.annotations.Permission
import nl.xandermarc.mc.lib.commands.annotations.PlayerOnly
import nl.xandermarc.mc.lib.commands.annotations.RankPermission
import nl.xandermarc.mc.lib.commands.annotations.Root
import nl.xandermarc.mc.lib.commands.arguments.*
import nl.xandermarc.mc.lib.extensions.nameSuffix
import nl.xandermarc.mc.lib.extensions.subclassOf
import nl.xandermarc.mc.lib.utils.Executor
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
 * The Command class is used to define custom commands with specific logic, including execution context,
 * player or entity restrictions, and integration with plugin lifecycles. It supports setting a name
 * and description, and can define execution behavior for both players and other command senders.
 *
 * @property name The name of the command. Defaults to the suffix of the class name if not provided.
 * @property description A brief description of the command's functionality.
 *
 * @constructor Creates a Command instance with an optional name and description.
 *
 * The Command class also integrates with Paper's lifecycle events to automatically register commands when
 * the plugin lifecycle reaches the appropriate stage.
 *
 * Example usage:
 *
 * ```kotlin
 * class MyCommand : Command(name = "example", description = "An example command") {
 *     override fun register(plugin: JavaPlugin) {
 *         super.register(plugin)
 *         // Additional registration logic here
 *     }
 * }
 * ```
 *
 * Notes:
 * - Commands marked with @PlayerOnly can only be executed by players, not by console or other entities.
 * - The class interacts with the plugin lifecycle manager to ensure commands are properly registered.
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

    override fun register(plugin: JavaPlugin) {
        plugin.lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) {
            it.registrar().register(commandName, description, this)
        }

        Tests.commandArguments(this)
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
        this::class.findAnnotation<RankPermission>()?.let {
            if (!it.rank.hasPermission(commandSourceStack.sender)) return noPermission(commandSourceStack, it.message)
        }
        source = commandSourceStack.sender
        executor = commandSourceStack.executor
        for (memberFunction in this::class.memberFunctions) {
            memberFunction.findAnnotation<Permission>()?.let {
                val permission = if (it.permission != "") it.permission else "command.$commandName.${memberFunction.name}"
                if (!commandSourceStack.sender.hasPermission(permission)) return noPermission(commandSourceStack, it.message)
            }
            memberFunction.findAnnotation<RankPermission>()?.let {
                if (!it.rank.hasPermission(commandSourceStack.sender)) return noPermission(commandSourceStack, it.message)
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
