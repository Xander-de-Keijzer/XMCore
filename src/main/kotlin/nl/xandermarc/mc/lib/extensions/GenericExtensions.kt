package nl.xandermarc.mc.lib.extensions

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import nl.xandermarc.mc.lib.data.Globals
import org.bukkit.plugin.java.JavaPlugin
import org.joml.Vector3d
import kotlin.math.sqrt
import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmName

inline fun <reified T> Any.asType(default: Any? = null, block: T.() -> Any?): Any? {
    return (this as? T)?.run(block) ?: default
}

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

fun evaluate(p0: Vector3d, p1: Vector3d, p2: Vector3d, p3: Vector3d, t: Double): Vector3d {
    val u = 1.0 - t
    val term0 = p0.mul(u.cubed(), Vector3d())                        // (1 - t)^3 * P0
    val term1 = p1.mul(3.0 * u.squared() * t, Vector3d())     // 3 * (1 - t)^2 * t * P1
    val term2 = p2.mul(3.0 * u * t.squared(), Vector3d())     // 3 * (1 - t) * t^2 * P2
    val term3 = p3.mul(t.cubed(), Vector3d())                       // t^3 * P3

    return term0.add(term1).add(term2).add(term3)
}

fun derivative(p0: Vector3d, p1: Vector3d, p2: Vector3d, p3: Vector3d, t: Double): Vector3d {
    val u = 1.0 - t
    val term0 = p1.sub(p0, Vector3d()).mul(3.0 * u.squared())        // 3 * (1 - t)^2 * (P1 - P0)
    val term1 = p2.sub(p1, Vector3d()).mul(6.0 * u * t)     // 6 * (1 - t) * t * (P2 - P1)
    val term2 = p3.sub(p2, Vector3d()).mul(3.0 * t.squared())        // 3 * t^2 * (P3 - P2)

    return term0.add(term1).add(term2)
}

fun secondDerivative(p0: Vector3d, p1: Vector3d, p2: Vector3d, p3: Vector3d, t: Double): Vector3d {
    val u = 1.0 - t
    val term0 = p2.sub(p1.mul(2.0, Vector3d()), Vector3d()).add(p0).mul(6.0 * u)  // 6 * (1 - t) * (P0 - 2*P1 + P2)
    val term1 = p3.sub(p2.mul(2.0, Vector3d()), Vector3d()).add(p1).mul(6.0 * t)  // 6 * t * (P1 - 2*P2 + P3)

    return term0.add(term1)
}

fun KClass<*>.nameSuffix(suffix: String): String =
    (simpleName ?: jvmName).lowercase().removeSuffix(suffix)

fun KClassifier?.subclassOf(kClass: KClass<*>): Boolean =
    (this as? KClass<*>)?.isSubclassOf(kClass) ?: false

fun Audience.send(message: String, vararg args: Any) {
    if (args.size == 1) {
        sendMessage(Globals.message.deserialize(message, Placeholder.parsed("0", "<gold>${args.first()}<gray>")))
    } else {
        val placeholders = args.mapIndexed { index, arg ->
            Placeholder.parsed("$index", "<gold>$arg<gray>")
        }
        sendMessage(Globals.message.deserialize(message, *placeholders.toTypedArray()))
    }
}
