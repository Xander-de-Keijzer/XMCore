package nl.xandermarc.mc.lib.commands

import io.papermc.paper.command.brigadier.Commands.literal
import nl.xandermarc.mc.rides.RideManager

object ExampleCommand : Command("example") {
    override val command = root

        .execute {
            source.sender.sendMessage("Root")
        }

        .then(literal("show")
            .execute {
                source.sender.sendMessage("Wow")
            }
        )

        .then(literal("list")
            .then(stringArgument("ride")
                .suggest(RideManager.rides.map { it.name })
                .execute {
                    val ride = getString("ride")
                    source.sender.sendMessage("Ride $ride")
                }
            )
        )

}
