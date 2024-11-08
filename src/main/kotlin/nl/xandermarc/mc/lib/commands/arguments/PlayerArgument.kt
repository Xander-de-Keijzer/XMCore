package nl.xandermarc.mc.lib.commands.arguments

import nl.xandermarc.mc.lib.commands.Argument
import nl.xandermarc.mc.lib.data.Globals
import org.bukkit.entity.Player
import java.util.*
import kotlin.reflect.KParameter

object PlayerArgument : Argument<Player>(Player::class) {
    override fun canParseArgument(args: List<String>, parameter: KParameter): Boolean {
        return if (Globals.players.any { it.name == args.first() }) {
            true
        } else {
            val uuid = try { UUID.fromString(args.first()) }
            catch (e: IllegalArgumentException) { return false }
            Globals.players.any { it.uniqueId == uuid }
        }
    }

    override fun parseArgument(args: List<String>, parameter: KParameter): Pair<Player, Int> {
        return Pair(
            Globals.players.firstOrNull { it.name == args.first() } ?:
            Globals.players.first { it.uniqueId == UUID.fromString(args.first()) },
            1
        )
    }
}