package nl.xandermarc.mc.lib.extensions

import net.kyori.adventure.text.Component
import nl.xandermarc.mc.lib.data.Globals
import org.bukkit.plugin.java.JavaPlugin
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.math.sqrt
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.jvmName

fun encode(a: Int, b: Int): Int {
    val (larger, smaller) = if (a > b) a to b else b to a
    return (larger * (larger - 1) / 2) + smaller
}

fun decode(num: Int): Pair<Int, Int> {
    val num2 = ((1 + sqrt(1.0 + 8.0 * num)) / 2).toInt()
    return Pair(num - num2 * (num2 - 1) / 2, num2)
}

fun <T, U> Iterable<T>.with(other: Iterable<U>, f: U.(T) -> Unit) =
    forEach { elm -> other.forEach { it.f(elm) } }

fun <T, U> Iterable<T>.apply(other: Iterable<U>, f: T.(U) -> Unit) =
    forEach { elm -> other.forEach { elm.f(it) } }

@Suppress("UnstableApiUsage")
val JavaPlugin.pluginName: String get() = pluginMeta.displayName

fun String.deserialize(vararg args: Any?): Component =
    Globals.message.deserialize(format(*args))

fun Component.plain(): String =
    Globals.plain.serialize(this)
