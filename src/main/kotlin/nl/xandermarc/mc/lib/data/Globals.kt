package nl.xandermarc.mc.lib.data

import MapSerializer
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import nl.xandermarc.mc.lib.extensions.syncExecutor
import org.bukkit.Location
import org.bukkit.Server
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database
import java.util.logging.Logger

object Globals {
    lateinit var instance: JavaPlugin

    val internalDB: Database = Database.connect("jdbc:h2:./h2db;", driver = "org.h2.Driver")
    val externalDB: Database get() = null!! // DO NOT USE YET
    val logger: Logger = Logger.getLogger("XMC")
    val message: MiniMessage = MiniMessage.miniMessage()
    val plain: PlainTextComponentSerializer = PlainTextComponentSerializer.plainText()
    val server: Server get() = instance.server
    val world: World get() = server.getWorld(Config.Defaults.WORLD) ?: server.worlds.first()
    val fallbackLocation: Location get() = Config.Defaults.LOCATION
    val players: Iterable<Player> get() = server.onlinePlayers
    val json: Json = Json {
        encodeDefaults = true
        explicitNulls = true
        ignoreUnknownKeys = true
        isLenient = false
        prettyPrint = true
        coerceInputValues = false
        useArrayPolymorphism = true
        allowSpecialFloatingPointValues = true
        allowStructuredMapKeys = false
        useArrayPolymorphism = false
        serializersModule = SerializersModule { contextual(MapSerializer) }
    }

    // Coroutines
    val jobs = hashMapOf<Job, String>()
    val supervisor = SupervisorJob()
    val syncScope = CoroutineScope(syncExecutor.asCoroutineDispatcher() + supervisor)
    val asyncScope = CoroutineScope(Dispatchers.IO + supervisor)
}
