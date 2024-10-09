package nl.xandermarc.mc.lib.async

object TaskManager {
    private val tasks = arrayListOf<Task>()

    fun register(job: Task) {
        tasks.add(job)
    }

    fun unregister(job: Task) {
        tasks.remove(job)
    }

    fun purgeNotActive() {
        tasks.removeIf { !it.active }
    }

    fun completeBlocking() {
        tasks.forEach { it.joinBlocking() }
        tasks.clear()
    }

    suspend fun complete() {
        tasks.forEach { it.join() }
        tasks.clear()
    }

    fun cancel() {
        tasks.forEach { it.cancel() }
        tasks.clear()
    }
}