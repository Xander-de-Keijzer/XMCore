package nl.xandermarc.mc.lib.data

@Suppress("unused")
object Colors {
    object Ansi {
        interface CodeProvider {
            fun code(): String
        }
        operator fun invoke(vararg codes: CodeProvider): String {
            val code = codes.joinToString(";") { it.code() }
            return "\u001B[${code}m"
        }
        data class Code(val code: Int): CodeProvider {
            override fun toString() = "\u001B[${code}m"
            override fun code() = code.toString()
            val bg: CodeProvider by lazy { Code(code + 10) }
        }
        data class Color8(val n: Int, val background: Boolean = false): CodeProvider {
            init { require(n in 0..255) { "Invalid 8-bit color number '$n'." } }
            constructor(r: Int, g: Int, b: Int): this(16+36*r+6*g+b)
            override fun code() = if (background) "48;5;$n" else "38;5;$n"
            val bg: CodeProvider by lazy { Color8(n, true) }
        }
        data class Color24(val r: Int, val g: Int, val b: Int, val background: Boolean = false): CodeProvider {
            init { require(r in 0..255) { "Color should be 8-bit (0-255) red='$r'." }
                   require(g in 0..255) { "Color should be 8-bit (0-255) green='$g'." }
                   require(b in 0..255) { "Color should be 8-bit (0-255) blue='$b'." }}
            override fun code() = if (background) "48;2;$r;$g;$b" else "38;2;$r;$g;$b"
        }

        val REGEX = Regex("\u001B\\[[\\d;]+m")
        val RESET = Code(0)
        val BOLD = Code(1)
        val FAINT = Code(2)
        val UNDERLINE = Code(4)
        val REVERSE = Code(7)
        val STRIKE = Code(9)
        val NORMAL = Code(22)

        val BLACK = Code(30)
        val RED = Code(31)
        val GREEN = Code(32)
        val YELLOW = Code(33)
        val BLUE = Code(34)
        val MAGENTA = Code(35)
        val CYAN = Code(36)
        val WHITE = Code(37)
        val BRIGHT_BLACK = Code(90)
        val BRIGHT_RED = Code(91)
        val BRIGHT_GREEN = Code(92)
        val BRIGHT_YELLOW = Code(93)
        val BRIGHT_BLUE = Code(94)
        val BRIGHT_MAGENTA = Code(95)
        val BRIGHT_CYAN = Code(96)
        val BRIGHT_WHITE = Code(97)
    }
}
