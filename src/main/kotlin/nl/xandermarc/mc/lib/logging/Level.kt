package nl.xandermarc.mc.lib.logging

class Level(val name: String, val ansi: String, val weight: Int) {
    companion object {
        val ALL = Level("ALL", "\u001B[0m", Int.MIN_VALUE)
        val TRACE = Level("TRACE", "\u001B[38;2;140;140;140m", 10)
        val DEBUG = Level("DEBUG", "\u001B[38;2;170;170;170m", 20)
        val INFO = Level("INFO", "\u001B[38;2;200;200;200m", 50)
        val WARN = Level("WARN", "\u001B[38;2;200;145;20m", 80)
        val ERROR = Level("ERROR", "\u001B[38;2;245;60;35m", 100)
        val NONE = Level("NONE", "\u001B[38;2;155;155;155m", Int.MAX_VALUE)
    }

    override fun toString() = name
    operator fun compareTo(other: Level) = other.weight - this.weight
}
