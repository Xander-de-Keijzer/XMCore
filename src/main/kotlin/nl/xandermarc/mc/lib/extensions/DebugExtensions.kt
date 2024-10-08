package nl.xandermarc.mc.lib.extensions

enum class LogPriority {
    INFO,
    WARN,
    ERROR,
    DEBUG,
}
inline fun log(priority: LogPriority = LogPriority.INFO, message: () -> String) {
    println("[${priority.name}@${Exception().stackTrace.first()}]: '${message()}'")
}