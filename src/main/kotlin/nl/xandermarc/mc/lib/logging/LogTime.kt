package nl.xandermarc.mc.lib.logging

import kotlinx.datetime.*
import nl.xandermarc.mc.lib.extensions.pad

data class LogTime(
    val time: LocalDateTime
) {
    constructor(instant: Instant): this(instant.toLocalDateTime(TimeZone.currentSystemDefault()))
    constructor(): this(Clock.System.now())

    val year = time.year.pad(4)
    val month = time.month.value.pad(2)
    val day = time.dayOfMonth.pad(2)
    val hour = time.hour.pad(2)
    val minute = time.minute.pad(2)
    val second = time.second.pad(2)
    val milli = (time.nanosecond / 1_000_000).pad(100)
    val micro = ((time.nanosecond % 1_000_000) / 1_000).pad(100)
    val nano = (time.nanosecond % 1_000).pad(100)

    fun logName(): String {
        return "$year-$month-${day}_${time.hour*3600+time.minute*60+time.second}"
    }
    override fun toString(): String {
        return "$hour:$minute:$second"
    }
}
