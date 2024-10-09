package nl.xandermarc.mc.lib.async

import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class Task(private var job: Job) {
    init { TaskManager.register(this) }

    val active get() = job.isActive
    val completed get() = job.isCompleted
    val cancelled get() = job.isCancelled

    fun joinBlocking() { runBlocking { join() } }
    suspend fun join() { if (active) job.join() }
    fun cancel() { job.cancel() }

    fun onComplete(task: () -> Unit): Task {
        if (active) {
            val completableJob = job
            job = Scopes.default.launch {
                completableJob.join()
                task()
            }
        } else {
            job = Scopes.default.launch {
                task()
            }
        }
        return this
    }
}