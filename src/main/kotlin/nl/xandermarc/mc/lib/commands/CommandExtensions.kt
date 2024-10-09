package nl.xandermarc.mc.lib.commands

import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.LongArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.Commands.argument
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.plugin.java.JavaPlugin

fun JavaPlugin.commandRegistrar(f: Commands.() -> Unit) {
    lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
        event.registrar().f()
    }
}

fun JavaPlugin.registerCommands(vararg commands: Command) {
    commandRegistrar {
        commands.forEach { command ->
            register(command.build())
        }
    }
}

fun <S, T : ArgumentBuilder<S, T>> T.execute(
    block: CommandContext<S>.() -> Unit
): T = executes {
    block(it)
    com.mojang.brigadier.Command.SINGLE_SUCCESS
}

fun <S, T : CommandContext<S>> T.getString(
    name: String
): String = StringArgumentType.getString(this, name)

fun <S, T : CommandContext<S>> T.getInt(
    name: String
): Int = IntegerArgumentType.getInteger(this, name)

fun <S, T : CommandContext<S>> T.getLong(
    name: String
): Long = LongArgumentType.getLong(this, name)

fun <S, T : CommandContext<S>> T.getFloat(
    name: String
): Float = FloatArgumentType.getFloat(this, name)

fun <S, T : CommandContext<S>> T.getDouble(
    name: String
): Double = DoubleArgumentType.getDouble(this, name)

fun stringArgument(name: String) = argument(name, StringArgumentType.word())
fun intArgument(name: String, min: Int = Int.MIN_VALUE, max: Int = Int.MAX_VALUE) = argument(name, IntegerArgumentType.integer(min, max))
fun longArgument(name: String, min: Long = Long.MIN_VALUE, max: Long = Long.MAX_VALUE) = argument(name, LongArgumentType.longArg(min, max))
fun floatArgument(name: String, min: Float = Float.MIN_VALUE, max: Float = Float.MAX_VALUE) = argument(name, FloatArgumentType.floatArg(min, max))
fun doubleArgument(name: String, min: Double = Double.MIN_VALUE, max: Double = Double.MAX_VALUE) = argument(name, DoubleArgumentType.doubleArg(min, max))

fun SuggestionsBuilder.suggestAll(suggestions: Iterable<String>) =
    suggestions.forEach { suggest(it) }

fun SuggestionsBuilder.suggestRemaining(suggestions: Iterable<String>) =
    suggestAll(suggestions.filter{ it.lowercase().startsWith(remainingLowerCase) })

fun RequiredArgumentBuilder<CommandSourceStack, String>.suggest(
    suggestions: Iterable<String>
) = suggests { ctx, builder ->
    builder.suggestRemaining(suggestions)
    builder.buildFuture()
}
