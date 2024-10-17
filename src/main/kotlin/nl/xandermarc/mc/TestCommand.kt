package nl.xandermarc.mc

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import nl.xandermarc.mc.lib.commands.Command
import nl.xandermarc.mc.lib.commands.executePlayer
import nl.xandermarc.mc.lib.math.toRadians
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemDisplay
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Transformation
import org.joml.Quaternionf
import org.joml.Vector3f

class TestCommand: Command("test") {
    override val command: LiteralArgumentBuilder<CommandSourceStack> = root
        .executePlayer {
            val location = it.location
            val display = location.world.spawnEntity(location, EntityType.ITEM_DISPLAY) as ItemDisplay
            val ypr = mutableListOf(0.0f, 0.0f, 0.0f)

            object : BukkitRunnable() {
                override fun run() {
                    if (ypr[0] < 360) ypr[0] = ypr[0] + 1
                    else if (ypr[1] < 360) ypr[1] = ypr[1] + 1
                    else if (ypr[2] < 360) ypr[2] = ypr[2] + 1
                    else {
                        display.remove()
                        cancel()
                    }

                    val qt = Quaternionf().rotateYXZ(ypr[0].toRadians(), ypr[1].toRadians(), ypr[2].toRadians())
                    display.transformation = Transformation(Vector3f(), qt, Vector3f(1f, 1f, 1f), Quaternionf())
                }
            }.runTaskTimer(XMCPlugin.instance, 0L, 1L)
        }
}
