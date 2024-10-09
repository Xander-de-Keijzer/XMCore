package nl.xandermarc.mc.lib.logging

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.char

object LogSettings {
    val level: LogPriority = LogPriority.DEBUG
    val dateTimeFormat = DateTimeComponents.Format {
        date(LocalDate.Formats.ISO); char(' '); hour(); char(':'); minute(); char(':'); second()
    }
    fun shouldLog(priority: LogPriority): Boolean {
        val logLevels = LogPriority.entries
        val levelIndex = logLevels.indexOf(level)
        val checkIndex = logLevels.indexOf(priority)
        return (checkIndex <= levelIndex)
    }
}
