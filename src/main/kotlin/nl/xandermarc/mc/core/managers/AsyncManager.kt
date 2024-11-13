package nl.xandermarc.mc.core.managers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import nl.xandermarc.mc.lib.extensions.launchAsync
import nl.xandermarc.mc.lib.interfaces.Manager
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

object AsyncManager : Manager {
    private val lock = ReentrantLock()
    private var enabled = false
    private val queue = PriorityQueue<Task> { task1, task2 ->
        task2.priority - task1.priority
    }

    fun offer(priority: Int, task: suspend CoroutineScope.() -> Unit) {
        lock.withLock {
            queue.offer(Task(priority, task))
        }
    }

    override fun enable(plugin: JavaPlugin): Manager {
        enabled = true
        launchAsync("AsyncManager") {
            var executing: Job? = null
            while(true) {
                lock.withLock {
                    if (!enabled) return@launchAsync
                    if (executing == null || executing?.isCompleted == true) {
                        val task = queue.poll()
                        if (task != null) {
                            executing = launchAsync("AsyncManager.task", task.block)
                            println("Started new task: $task")
                        }
                    }
                }
                delay(10)
            }
        }
        return this
    }

    override fun disable(): Manager {
        lock.withLock { enabled = false }
        return this
    }

    class Task(
        val priority: Int,
        val block: suspend CoroutineScope.() -> Unit
    ) {
        var cancelled = false
    }
}