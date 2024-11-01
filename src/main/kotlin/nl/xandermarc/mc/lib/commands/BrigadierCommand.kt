package nl.xandermarc.mc.lib.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands.literal
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import nl.xandermarc.mc.lib.data.Globals

@Suppress("UnstableApiUsage")
abstract class BrigadierCommand(val name: String) {
    protected val root: LiteralArgumentBuilder<CommandSourceStack> = literal(name)
    protected abstract val command: LiteralArgumentBuilder<CommandSourceStack>
    fun register() {
        Globals.instance.lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
            event.registrar().register(command.build())
        }
    }
}
