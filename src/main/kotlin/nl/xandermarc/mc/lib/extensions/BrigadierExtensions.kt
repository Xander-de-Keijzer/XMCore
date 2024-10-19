package nl.xandermarc.mc.lib.extensions

import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands.argument
import org.bukkit.entity.Player

fun <T : ArgumentBuilder<CommandSourceStack, T>> T.executePlayer(
    block: CommandContext<CommandSourceStack>.(Player) -> Unit
): T = executes {
    if (it.source.sender is Player) {
        block(it, it.source.sender as Player)
        com.mojang.brigadier.Command.SINGLE_SUCCESS
    }
    0
}

fun <T : ArgumentBuilder<CommandSourceStack, T>> T.execute(
    block: CommandContext<CommandSourceStack>.() -> Unit
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

fun stringArgument(name: String): RequiredArgumentBuilder<CommandSourceStack, String> =
    argument(name, StringArgumentType.word())

fun intArgument(
    name: String,
    min: Int = Int.MIN_VALUE,
    max: Int = Int.MAX_VALUE
): RequiredArgumentBuilder<CommandSourceStack, Int> =
    argument(name, IntegerArgumentType.integer(min, max))

fun longArgument(
    name: String,
    min: Long = Long.MIN_VALUE,
    max: Long = Long.MAX_VALUE
): RequiredArgumentBuilder<CommandSourceStack, Long> =
    argument(name, LongArgumentType.longArg(min, max))

fun floatArgument(
    name: String,
    min: Float = Float.MIN_VALUE,
    max: Float = Float.MAX_VALUE
): RequiredArgumentBuilder<CommandSourceStack, Float> =
    argument(name, FloatArgumentType.floatArg(min, max))

fun doubleArgument(
    name: String,
    min: Double = Double.MIN_VALUE,
    max: Double = Double.MAX_VALUE
): RequiredArgumentBuilder<CommandSourceStack, Double> =
    argument(name, DoubleArgumentType.doubleArg(min, max))

fun SuggestionsBuilder.suggestAll(suggestions: Iterable<String>) =
    suggestions.forEach { suggest(it) }

fun SuggestionsBuilder.suggestRemaining(suggestions: Iterable<String>) =
    suggestAll(suggestions.filter { it.lowercase().startsWith(remainingLowerCase) })

fun RequiredArgumentBuilder<CommandSourceStack, String>.suggest(
    suggestions: Iterable<String>
): RequiredArgumentBuilder<CommandSourceStack, String>? = suggests { _, builder ->
    builder.suggestRemaining(suggestions)
    builder.buildFuture()
}
