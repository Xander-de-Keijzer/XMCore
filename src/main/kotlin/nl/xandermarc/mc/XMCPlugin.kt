package nl.xandermarc.mc

import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import nl.xandermarc.mc.lib.commands.registerCommands
import nl.xandermarc.mc.lib.logging.*
import nl.xandermarc.mc.rides.RideManager
import nl.xandermarc.mc.rides.objects.TestTrackedRide
import nl.xandermarc.mc.rides.tracked.TrackManager
import org.bukkit.Server
import org.bukkit.World
import org.bukkit.plugin.java.JavaPlugin
import java.nio.file.Files
import java.util.logging.Level
import kotlin.io.path.exists

class XMCPlugin : JavaPlugin() {

    companion object {
        lateinit var instance: JavaPlugin
        lateinit var settings: Settings

        val server: Server by lazy { instance.server }
        val world: World by lazy { server.getWorld(settings.defaultWorld) ?: server.worlds.first() }

        // Coroutines
        private val writeJob = SupervisorJob()
        private val readJob = SupervisorJob()
        val writeScope = CoroutineScope(Dispatchers.IO + writeJob)
        val readScope = CoroutineScope(Dispatchers.IO + readJob)
    }

    /*   Plugin enable   */

    @Suppress("UnstableApiUsage")
    override fun onEnable() {
        instance = this

        reloadSettings()
        registerRides()
        registerCommands()

        logger.info("${pluginMeta.displayName} has been enabled.")
    }

    @Serializable
    data class Settings(
        var defaultWorld: String = "world",
        var asyncWriteTimeout: Long = 20000L,
        var asyncReadTimeout: Long = 5000L,
        var asyncCancelTimeout: Long = 3000L,
        @Serializable(with = LevelSerializer::class)
        var logLevel: Level = Level.INFO
    )

    private fun registerRides() = RideManager.register(
        TestTrackedRide
    )

    private fun registerCommands() = registerCommands(
        TrackManager
    )

    /*   Plugin disable   */

    @Suppress("UnstableApiUsage")
    override fun onDisable() {
        completeJobs()
        logger.info("${pluginMeta.displayName} has been disabled.")
    }

    private fun completeJobs() {
        if (writeJob.children.any { it.isActive }) completeJobs(writeJob, "Writing", settings.asyncWriteTimeout)
        if (readJob.children.any { it.isActive }) completeJobs(readJob, "Reading", settings.asyncReadTimeout)
        writeJob.cancel()
        readJob.cancel()
    }

    private fun completeJobs(job: Job, name: String, timeout: Long) {
        val lowerName = name.lowercase()
        runBlocking {
            try {
                info("Plugin is disabling whilst running asynchronous $lowerName tasks, waiting $timeout ms for them to complete.")
                withTimeout(timeout) {
                    job.children.forEach { it.join() }
                }
            } catch (_: TimeoutCancellationException) {
                warn("Some asynchronous $lowerName jobs did not complete within the timeout, waiting ${settings.asyncCancelTimeout} ms for them to cancel.")
                job.children.forEach { childJob ->
                    try {
                        withTimeout(settings.asyncCancelTimeout) {
                            childJob.cancelAndJoin()
                        }
                    } catch (_: TimeoutCancellationException) {
                        error("$name job $childJob failed to cancel within ${settings.asyncCancelTimeout}ms.")
                    }
                }
            } finally {
                if (job.children.any { it.isActive }) {
                    job.cancel()
                    warn("Asynchronous $lowerName scope was cancelled.")
                } else {
                    info("All $lowerName tasks cancelled successfully.")
                }
            }
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun reloadSettings() = runBlocking {
        val path = dataPath.resolve("settings.json")
        val codec = Json { encodeDefaults = true; ignoreUnknownKeys = true; isLenient = true; prettyPrint = true }
        if (path.exists()) {
            readScope.launch {
                settings = codec.decodeFromString(Files.readString(path))
                Files.delete(path)
            }.join()
        } else {
            settings = Settings()
        }
        writeScope.launch {
            Files.createDirectories(path.parent)
            Files.createFile(path)
            Files.writeString(path, codec.encodeToString(settings))
        }
    }

}
