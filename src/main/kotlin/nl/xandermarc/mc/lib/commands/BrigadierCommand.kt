package nl.xandermarc.mc.lib.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands.literal
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import nl.xandermarc.mc.lib.utils.Executor
import org.bukkit.plugin.java.JavaPlugin

@Suppress("UnstableApiUsage")
abstract class BrigadierCommand(val name: String) : Executor {
    protected val root: LiteralArgumentBuilder<CommandSourceStack> = literal(name)
    protected abstract val command: LiteralArgumentBuilder<CommandSourceStack>
    override fun register(plugin: JavaPlugin) {
        plugin.lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
            event.registrar().register(command.build())
        }
    }
}
