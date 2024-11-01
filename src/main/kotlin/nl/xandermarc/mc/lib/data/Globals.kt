package nl.xandermarc.mc.lib.data

import kotlinx.coroutines.*
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import nl.xandermarc.mc.lib.extensions.syncDispatcher
import org.bukkit.Location
import org.bukkit.Server
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

object Globals {
    lateinit var instance: JavaPlugin

    private const val DEFAULT_WORLD = "world"
    const val JOIN_MESSAGE = "<green>%s Joined the game"
    const val QUIT_MESSAGE = "<red>%s Left the game"
    const val PACKET_EVENT_ENABLED = false

    val logger: Logger = Logger.getLogger("XMC")
    val message: MiniMessage = MiniMessage.miniMessage()
    val plain: PlainTextComponentSerializer = PlainTextComponentSerializer.plainText()
    val server: Server get() = instance.server
    val world: World get() = server.getWorld(DEFAULT_WORLD) ?: server.worlds.first()
    val fallbackLocation: Location get() = Location(world, 0.0, 0.0, 0.0)
    val players: Iterable<Player> get() = server.onlinePlayers

    // Coroutines
    val jobs = hashMapOf<Job, String>()
    val supervisor = SupervisorJob()
    val syncScope = CoroutineScope(syncDispatcher() + supervisor)
    val writeScope = CoroutineScope(Dispatchers.IO + supervisor)
    val readScope = CoroutineScope(Dispatchers.IO + supervisor)
}
