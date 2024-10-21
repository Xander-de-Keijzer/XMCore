package nl.xandermarc.mc.core

import kotlinx.coroutines.*
import nl.xandermarc.mc.lib.extensions.error
import nl.xandermarc.mc.lib.extensions.info
import nl.xandermarc.mc.lib.extensions.launchJob
import nl.xandermarc.mc.lib.extensions.warn
import org.bukkit.Location
import org.bukkit.Server
import org.bukkit.World
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

object XMC {
    lateinit var instance: JavaPlugin
        private set

    private const val DEFAULT_WORLD = "world"
    private const val ASYNC_WRITE_TIMEOUT = 20000L
    private const val ASYNC_READ_TIMEOUT = 5000L
    private const val ASYNC_CANCEL_TIMEOUT = 3000L

    val logger: Logger = Logger.getLogger("XMC")
    val server: Server get() = instance.server
    val world: World get() = server.getWorld(DEFAULT_WORLD) ?: server.worlds.first()
    val fallbackLocation: Location get() = Location(world, 0.0, 0.0, 0.0)

    // Coroutines
    val jobs = hashMapOf<Job, String>()
    private val writeJob = SupervisorJob()
    private val readJob = SupervisorJob()
    private val writeScope = CoroutineScope(Dispatchers.IO + writeJob)
    private val readScope = CoroutineScope(Dispatchers.IO + readJob)

    fun onLoad(plugin: JavaPlugin) { instance = plugin }
    fun enable() {}
    fun disable() { completeJobs() }

    fun launchReadJob(name: String = "Unknown", block: suspend CoroutineScope.() -> Unit): Job =
        readScope.launchJob("ReadJob($name)", block)

    fun launchWriteJob(name: String = "Unknown", block: suspend CoroutineScope.() -> Unit): Job =
        writeScope.launchJob("WriteJob($name)", block)

    private fun completeJobs() {
        if (writeJob.children.any { it.isActive }) completeJobs(writeJob, "Writing", ASYNC_WRITE_TIMEOUT)
        if (readJob.children.any { it.isActive }) completeJobs(readJob, "Reading", ASYNC_READ_TIMEOUT)
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
                warn("Some asynchronous $lowerName jobs did not complete within the timeout, waiting $ASYNC_CANCEL_TIMEOUT ms for them to cancel.")
                job.children.forEach { childJob ->
                    try {
                        withTimeout(ASYNC_CANCEL_TIMEOUT) {
                            childJob.cancelAndJoin()
                        }
                    } catch (_: TimeoutCancellationException) {
                        error("$name job $childJob failed to cancel within $ASYNC_CANCEL_TIMEOUT ms.")
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
