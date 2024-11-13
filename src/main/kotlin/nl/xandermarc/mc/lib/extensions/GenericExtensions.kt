@file:JvmName("Extensions")
@file:JvmMultifileClass
package nl.xandermarc.mc.lib.extensions

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import nl.xandermarc.mc.lib.data.Globals
import org.bukkit.plugin.java.JavaPlugin

@Suppress("UnstableApiUsage")
val JavaPlugin.pluginName: String get() = pluginMeta.displayName

fun <T, U> Iterable<T>.with(other: Iterable<U>, f: U.(T) -> Unit) =
    forEach { elm -> other.forEach { it.f(elm) } }

fun <T, U> Iterable<T>.apply(other: Iterable<U>, f: T.(U) -> Unit) =
    forEach { elm -> other.forEach { elm.f(it) } }

fun Component.plain(): String = Globals.plain.serialize(this)

fun String.component(vararg args: Any): Component {
    if (args.size == 1) {
        return Globals.message.deserialize(this, Placeholder.parsed("0", "<gold>${args.first()}<gray>"))
    } else {
        val placeholders = args.mapIndexed { index, arg ->
            Placeholder.parsed("$index", "<gold>$arg<gray>")
        }
        return Globals.message.deserialize(this, *placeholders.toTypedArray())
    }
}

fun Audience.send(message: String, vararg args: Any) = sendMessage(message.component(*args))
