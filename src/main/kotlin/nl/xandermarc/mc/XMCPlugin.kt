package nl.xandermarc.mc

import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import nl.xandermarc.mc.lib.commands.registerCommands
import nl.xandermarc.mc.lib.logging.LevelSerializer
import nl.xandermarc.mc.lib.logging.error
import nl.xandermarc.mc.lib.logging.info
import nl.xandermarc.mc.lib.logging.warn
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
        val world: World by lazy {
            server.getWorld(settings.defaultWorld) ?: server.worlds.first()
        }

        // Coroutines
        private val writeJob = SupervisorJob()
        private val readJob = SupervisorJob()
        val writeScope = CoroutineScope(Dispatchers.IO + writeJob)
        val readScope = CoroutineScope(Dispatchers.IO + readJob)
    }

    /*

    Plugin enable

     */

    /**
     * Handles the actions required to initialize the plugin when it is enabled.
     *
     * Assigns the current instance to a singleton reference,
     * registers necessary rides and commands,
     * and logs a message indicating that the plugin has been enabled.
     */
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

    fun reloadSettings() = runBlocking {
        val path = dataPath.resolve("settings.json")
        val codec = Json {
            encodeDefaults = true
            explicitNulls = true
            ignoreUnknownKeys = true
            isLenient = true
            prettyPrint = true
            useAlternativeNames = false
        }
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

    /**
     * This method is intended to be called during the initialization
     * phase of the application to ensure that all rides are
     * properly registered and available for use.
     *
     * The RideManager handles the lifecycle management of rides,
     * including enabling and disabling, and ensures that rides
     * are registered only once.
     */
    private fun registerRides() = RideManager.register(
        TestTrackedRide
    )

    /**
     * Registers the specified commands for the plugin using the command registration system.
     * This function wraps the plugin's command registering extension and registers each provided command.
     */
    private fun registerCommands() = registerCommands(
        TrackManager
    )

    /*

    Plugin disable

     */

    /**
     * Handles the plugin's disable event.
     *
     * This method is called when the plugin is disabled. It is responsible for
     * completing any remaining asynchronous jobs and logging the event that the
     * plugin has been disabled.
     */
    override fun onDisable() {
        completeJobs()
        logger.info("${pluginMeta.displayName} has been disabled.")
    }

    /**
     * Completes any active read or write jobs and cancels their respective scopes.
     *
     * This method performs the following:
     * 1. Checks if there are any active children jobs within the `writeJob` or `readJob` scopes.
     * 2. If there are active jobs, it invokes the overloaded `completeJobs` method to handle them,
     *    using appropriate timeout settings for writing and reading tasks.
     * 3. Cancels the `writeJob` and `readJob` scopes to ensure no further tasks are executed.
     */
    private fun completeJobs() {
        if (writeJob.children.any { it.isActive }) completeJobs(writeJob, "Writing", settings.asyncWriteTimeout)
        if (readJob.children.any { it.isActive }) completeJobs(readJob, "Reading", settings.asyncReadTimeout)
        writeJob.cancel()
        readJob.cancel()
    }

    /**
     * Completes and cancels the given job and its children within specified timeouts.
     *
     * @param job The root job that will be monitored and eventually cancelled.
     * @param name A descriptive name of the job, used in log messages.
     * @param timeout The maximum time in milliseconds to wait for the job and its children to complete.
     */
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

}
