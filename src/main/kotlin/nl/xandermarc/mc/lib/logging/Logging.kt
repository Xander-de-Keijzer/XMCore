package nl.xandermarc.mc.lib.logging

import nl.xandermarc.mc.core.sql.asyncTransaction
import nl.xandermarc.mc.core.sql.entities.LogEntry
import nl.xandermarc.mc.lib.data.Globals
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.PrintStream
import kotlin.io.path.appendText
import kotlin.io.path.createFile

object Logging {
    @Suppress("MemberVisibilityCanBePrivate")
    var level = Level.INFO
    var printStackTraces = true
    private val loggers = mutableListOf<Logger>()
    private val printStream = PrintStream(FileOutputStream(FileDescriptor.out), true)
    private val logFile = Globals.logsPath.resolve("${LogTime().logName()}.log").createFile()

    val logger get() = LoggerImpl().apply { register(this) }

    fun logger(name: String) : Logger {
        val existing = loggers.firstOrNull { it.logName == name }
        if (existing != null) return existing
        val logger = LoggerImpl(name)
        register(logger)
        return logger
    }

    fun register(logger: Logger) {
        if (loggers.any { it.logName == logger.logName }) { throw IllegalStateException("A logger with name ${logger.logName} already exists.") }
        loggers.add(logger)
    }

    fun Logger.log(level: Level, message: () -> Any, throwable: Throwable? = null) {
        val effectiveLevel = logLevel ?: parentLevel(logName)
        val item = LogItem(logName, level, message().toString(), throwable)
        logItem(item, effectiveLevel)
    }

    fun raw(name: String, message: () -> Any) {
        val item = LogItem(name, Level.NONE, message().toString(), null)
        logFile.appendText("${item.noColor()}\n")
        printItem(item)
    }

    private fun logItem(item: LogItem, loggingLevel: Level) {
        logFile.appendText("${item.noColor()}\n")
        asyncTransaction("log(${item.name})") {
            LogEntry.new {
                this.created = item.time.time
                this.levelName = item.level.name
                this.levelWeight = item.level.weight
                this.loggerName = item.name
                this.message = item.msgNoColor()
            }
        }
        if (item.level <= loggingLevel) printItem(item)
    }

    private fun printItem(item: LogItem) {
        if (item.throwable != null) {
            logItem(LogItem(item.name, Level.TRACE, "${item.level.ansi}${item.throwable}", null), Level.ALL)
            item.throwable.stackTrace.forEach { traceElement ->
                logItem(LogItem(item.name, Level.TRACE, "${item.level.ansi} at $traceElement", null), Level.ALL)
            }
        }
        printStream.println(item)
    }

    private fun parentLevel(name: String): Level {
        var cur = name.lowercase()
        while (cur.contains(".")) {
            val lastDot = cur.lastIndexOf(".")
            if (lastDot == -1) break
            cur = cur.substring(0, lastDot)
            val level = loggers.firstOrNull { it.logName == cur }?.logLevel ?: continue
            return level
        }
        return level
    }
}
