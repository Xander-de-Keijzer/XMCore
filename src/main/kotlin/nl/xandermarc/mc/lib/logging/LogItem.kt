package nl.xandermarc.mc.lib.logging

import nl.xandermarc.mc.lib.data.Colors.Ansi.REGEX
import kotlin.reflect.jvm.jvmName

data class LogItem(
    val name: String,
    val level: Level,
    val message: String,
    val throwable: Throwable?
) { val time = LogTime()

    fun noColor(): String {
        return REGEX.replace(toString(), "")
    }

    fun msgNoColor(): String {
        return REGEX.replace(message, "")
    }

    override fun toString(): String {
        val textColor = if (level in listOf(Level.WARN, Level.ERROR)) level.ansi else ""
        val nameColor = if (level in listOf(Level.WARN, Level.ERROR)) LIGHT_GRAY else WHITE
        if (throwable == null) return FORMAT.format(level.ansi, time, level, nameColor, name, textColor, message)
        return THROW_FORMAT.format(level.ansi, time, level, nameColor, name, textColor, message, nameColor, throwable::class.jvmName, throwable.message)
    }

    companion object {
        private const val RESET = "\u001B[0;22m"
        private const val WHITE = "\u001B[38;2;220;220;220m"
        private const val LIGHT_GRAY = "\u001B[38;2;160;160;160m"
        private const val GRAY = "\u001B[38;2;120;120;120m"
        private const val DARK_GRAY = "\u001B[38;2;60;60;60m"
        const val FORMAT = "$RESET$GRAY[$WHITE%s%s %s$GRAY]($WHITE%s%s$GRAY)$DARK_GRAY: $WHITE%s%s$RESET"
        const val THROW_FORMAT = "$FORMAT $GRAY{$WHITE%s%s %s$GRAY}$RESET"
    }

}
