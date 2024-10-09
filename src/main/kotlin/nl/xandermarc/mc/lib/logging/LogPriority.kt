package nl.xandermarc.mc.lib.logging

enum class LogPriority {
    ERROR,
    WARN,
    INFO,
    DEBUG;

    override fun toString(): String = name.lowercase()
}
