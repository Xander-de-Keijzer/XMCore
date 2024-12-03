package nl.xandermarc.mc.lib.logging

interface Logger {
    var logName: String
    var logLevel: Level?
    fun trace(throwable: Throwable? = null, message: () -> Any)
    fun debug(throwable: Throwable? = null, message: () -> Any)
    fun info(throwable: Throwable? = null, message: () -> Any)
    fun warn(throwable: Throwable? = null, message: () -> Any)
    fun error(throwable: Throwable? = null, message: () -> Any)
    fun Any.logInit() { logName = this::class.java.packageName.lowercase() }
}
