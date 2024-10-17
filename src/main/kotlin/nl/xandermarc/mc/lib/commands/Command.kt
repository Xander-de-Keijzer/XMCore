package nl.xandermarc.mc.lib.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands.literal

@Suppress("UnstableApiUsage")
abstract class Command(val name: String) {
    protected val root: LiteralArgumentBuilder<CommandSourceStack> = literal(name)
    protected abstract val command: LiteralArgumentBuilder<CommandSourceStack>
    fun build(): LiteralCommandNode<CommandSourceStack> = command.build()
}
