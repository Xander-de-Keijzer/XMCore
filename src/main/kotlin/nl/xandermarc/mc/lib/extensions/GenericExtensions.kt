package nl.xandermarc.mc.lib.extensions

import org.bukkit.plugin.java.JavaPlugin
import kotlin.math.sqrt

fun encode(a: Int, b: Int): Int {
    val (larger, smaller) = if (a > b) a to b else b to a
    return (larger * (larger - 1) / 2) + smaller
}
fun decode(num: Int): Pair<Int, Int> {
    val num2 = ((1 + sqrt(1.0 + 8.0 * num)) / 2).toInt()
    return Pair(num - num2 * (num2 - 1) / 2, num2)
}

fun <T, U> Iterable<T>.onEach(f: T.(U) -> Unit, other: Iterable<U>) =
    forEach { elm -> other.forEach { elm.f(it) } }

@Suppress("UnstableApiUsage")
val JavaPlugin.pluginName: String get() = pluginMeta.displayName
