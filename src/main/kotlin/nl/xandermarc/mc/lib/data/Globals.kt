package nl.xandermarc.mc.lib.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.minecraft.core.RegistryAccess
import net.minecraft.server.MinecraftServer
import nl.xandermarc.mc.lib.extensions.syncExecutor
import nl.xandermarc.mc.lib.logging.Logger
import nl.xandermarc.mc.lib.logging.Logging
import nl.xandermarc.mc.lib.serializers.MapSerializer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Server
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database
import java.nio.file.Files
import java.nio.file.Path

object Globals {
    const val PLUGIN_NAME = "xmc"
    var lateInstance: JavaPlugin? = null
        set(value) { field = value; if (value != null) instance = value }
    lateinit var instance: JavaPlugin

    val internalDB: Database = Database.connect("jdbc:h2:./h2db;", driver = "org.h2.Driver")
    val externalDB: Database get() = null!! // DO NOT USE YET
    val logger: Logger = Logging.logger(PLUGIN_NAME)
    val message: MiniMessage = MiniMessage.miniMessage()
    val plain: PlainTextComponentSerializer = PlainTextComponentSerializer.plainText()
    val server: Server get() = instance.server
    val pluginPath: Path get() = Bukkit.getPluginsFolder().toPath().resolve(PLUGIN_NAME)
    val logsPath: Path get() = pluginPath.resolve("logs").apply { Files.createDirectories(this) }
    val world: World get() = server.getWorld(Config.Defaults.WORLD) ?: server.worlds.first()
    val fallbackLocation: Location get() = Config.Defaults.LOCATION
    val players: Collection<Player> get() = server.onlinePlayers
    val registryAccess: RegistryAccess = MinecraftServer.getServer().registryAccess()
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
    val supervisor = SupervisorJob()
    val syncScope = CoroutineScope(syncExecutor.asCoroutineDispatcher() + supervisor)
    val asyncScope = CoroutineScope(Dispatchers.IO + supervisor)
}
