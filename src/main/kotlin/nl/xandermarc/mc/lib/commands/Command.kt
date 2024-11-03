package nl.xandermarc.mc.lib.commands

import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import nl.xandermarc.mc.lib.commands.annotations.Options
import nl.xandermarc.mc.lib.commands.annotations.PlayerOnly
import nl.xandermarc.mc.lib.commands.annotations.Range
import nl.xandermarc.mc.lib.commands.annotations.Root
import nl.xandermarc.mc.lib.data.Globals
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.*
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KVisibility
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.jvmName

@Suppress("UnstableApiUsage")
abstract class Command(
    val name: String? = null,
    val description: String? = null,
) : BasicCommand {
    protected var source: CommandSender? = null
    protected var executor: Entity? = null
    protected val player: Player
        get() = source as? Player ?: throw IllegalStateException(
            "accessing player is only safe when function is marked with @PlayerOnly"
        )

    fun register() {
        val className = (this::class.simpleName ?: this::class.jvmName).lowercase()
        val commandName = if (className.endsWith("command")) className.dropLast(7) else className
        Globals.instance.lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) {
            it.registrar().register(name ?: commandName, description, this)
        }
    }

    private fun canParseArgument(args: MutableList<String>, parameter: KParameter): Boolean {
        if (args.isEmpty()) return false
        if (parameter.isOptional) return true
        return when(parameter.type.classifier) {
            Unit::class -> args.first() == parameter.name
            String::class -> {
                parameter.findAnnotation<Options>()?.run {
                    return args.first() in options
                }
                true
            }
            Int::class -> {
                val i = try { args.first().toInt() }
                catch (e: NumberFormatException) { return false }
                parameter.findAnnotation<Range>()?.run {
                    return i in min..max
                }
                true
            }
            Player::class -> {
                if (Globals.players.any { it.name == args.first() }) true
                else {
                    val uuid = try { UUID.fromString(args.first()) }
                    catch (e: IllegalArgumentException) { return false }
                    Globals.players.any { it.uniqueId == uuid }
                }
            }
            else -> false
        }
    }

    private fun parseArgument(args: MutableList<String>, parameter: KParameter): Pair<Any, Int> {
        return when(parameter.type.classifier) {
            Unit::class -> Pair(Unit, 1)
            String::class -> Pair(args.first(), 1)
            Int::class -> Pair(args.first().toInt(), 1)
            Player::class -> Pair(
                Globals.players.firstOrNull { it.name == args.first() } ?:
                Globals.players.first { it.uniqueId == UUID.fromString(args.first()) },
                1
            )
            else -> throw IllegalStateException()
        }
    }

    private fun executeCommandOnMemberFunction(arguments: MutableList<String>, memberFunction: KFunction<*>) {
        var args = arguments
        if (memberFunction.hasAnnotation<PlayerOnly>() && source !is Player) return
        if (args.isEmpty() && memberFunction.parameters.size == 1) {
            memberFunction.call(this)
        } else {
            val build = hashMapOf<KParameter, Any>()
            build[memberFunction.parameters.first()] = this
            for (parameter in memberFunction.parameters.drop(1)) {
                if (!canParseArgument(args, parameter)) return
                val (arg, taken) = parseArgument(args, parameter)
                args = args.drop(taken).toMutableList()
                build[parameter] = arg
            }
            if (args.isEmpty()) memberFunction.callBy(build)
        }
    }

    override fun execute(commandSourceStack: CommandSourceStack, args: Array<out String>) {
        source = commandSourceStack.sender
        executor = commandSourceStack.executor
        for (memberFunction in this::class.memberFunctions) {
            if (memberFunction.visibility != KVisibility.PUBLIC) continue
            if (memberFunction.name in listOf("canUse", "equals", "execute", "hashCode", "permission", "register", "suggest", "toString")) continue
            memberFunction.findAnnotation<Root>()?.let {
                executeCommandOnMemberFunction(args.toMutableList(), memberFunction)
            }
            if (args.isNotEmpty() && args.first() == memberFunction.name) {
                executeCommandOnMemberFunction(
                    args.drop(1).toMutableList(),
                    memberFunction
                )
            }
        }
    }
}
