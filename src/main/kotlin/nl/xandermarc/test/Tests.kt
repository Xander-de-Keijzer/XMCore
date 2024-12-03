package nl.xandermarc.test

import net.minecraft.world.entity.EntityType
import nl.xandermarc.mc.lib.commands.Command
import nl.xandermarc.mc.lib.commands.annotations.Greedy
import nl.xandermarc.mc.lib.data.EntityData
import nl.xandermarc.mc.lib.data.Globals
import nl.xandermarc.mc.lib.extensions.*
import nl.xandermarc.mc.lib.packets.BundlePacket
import nl.xandermarc.mc.lib.packets.Packet
import nl.xandermarc.mc.lib.packets.entities.AddEntityPacket
import nl.xandermarc.mc.lib.packets.entities.MoveEntityPacket
import nl.xandermarc.mc.lib.packets.entities.SetEntityDataPacket
import nl.xandermarc.mc.lib.packets.entities.TeleportEntityPacket
import nl.xandermarc.mc.ride.Cart
import nl.xandermarc.mc.ride.Ride
import nl.xandermarc.mc.ride.Train
import nl.xandermarc.mc.ride.entity.Display
import nl.xandermarc.mc.ride.entity.Rideable
import org.bukkit.inventory.ItemStack
import org.joml.Quaterniond
import org.joml.Vector3d
import kotlin.math.floor
import kotlin.math.max
import kotlin.reflect.KClass
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberFunctions
import kotlin.system.measureNanoTime

object Tests {
    fun commandArguments(command: Command) {
        command::class.memberFunctions.filter { it.parameters.size > 1 }.forEach { func ->
            if (func.parameters.dropLast(1).any {
                    it.type.classifier == List::class || it.hasAnnotation<Greedy>()
                }) {
                Globals.logger.warn {
                    "Function '${func.name}(${
                        func.parameters.drop(1)
                            .joinToString(", ") { "${it.name}: ${(it.type.classifier as KClass<*>).simpleName}" }
                    })' of command '${command::class.simpleName}' has an argument of type list or greedy string\n" +
                            "which will take all remaining arguments from the stack, but has more arguments defined after making the function unsatisfiable."
                }
            }
        }
    }
}

object TestRide : Ride("test") {
    private val train = Train.Builder(
        Cart(
            sizeFront=5.0, sizeRear=5.0,
            Display(offset=vec3d(), model=1)
        ),
        Cart(
            sizeFront=15.0, sizeRear=15.0,
            Display(offset=vec3d(), model=2),
            Rideable(offset=vec3d(x=1.0, z=0.5)), //FL
            Rideable(offset=vec3d(x=-1.0, z=0.5)), //FR
            Rideable(offset=vec3d(x=1.0, z=-0.5)), //BL
            Rideable(offset=vec3d(x=-1.0, z=-0.5)), //BR
        ).copy(5)
    )

    object Station : Controller() {
        var dispatching = false
        override fun onTrainTick(train: Train) {
            if ((train.position?.position ?: 0.0) > 100.0) {
                train.speed = max(train.speed - 1.0, 0.0)
            }
            println(dispatching) // Available from station
            println(segment) // Available from controller
            println(tracks) // Available from ride
            println(trains) // Available from Ride
            dispatching = !dispatching
        }
    }

    override fun load() {
        loadTrack("test") {
            addTrain(train, segment=0, position=10.0, direction=true)
            addTrain(train, segment=1, position=20.0, direction=true)

            segment(1).controller = Station
            segment(0).controller = Station // Segment 1 will lose this controller
        }
    }

    override fun remove() {

    }
}

class Callback(private val innerCallback: (Array<out Any>) -> Unit) {
    operator fun invoke(vararg args: Any) {
        innerCallback(args)
    }
}

sealed class Entity(
    val type: EntityType<*>,
    location: Vector3d = Vector3d(),
) {
    private val lastCorrectLocation = location
    val id = 1
    var location = location
        set(value) {
            if (field == value) return
            if (location.distanceSquared(lastCorrectLocation) > 4*4) {
                val block = Vector3d(floor(location.x), floor(location.x), floor(location.x))
                val offset = Vector3d(location).sub(block)
                updates.add(TeleportEntityPacket(id, block))
                updates.add(MoveEntityPacket(id, offset))
            } else {
                updates.add(MoveEntityPacket(id, field, value))
            }
            field = value
        }

    protected val updates = mutableListOf<Packet<*>>()
    val getUpdates get() = BundlePacket(updates).also { updates.clear() }
    val addEntity get() = BundlePacket(listOf(addEntityPacket).plus(extraPackets))
    private val addEntityPacket get() = AddEntityPacket(id, type, location)
    protected abstract val extraPackets: List<Packet<*>>
}

class DisplayEntity(
    item: ItemStack,
    location: Vector3d = Vector3d(),
    rotation: Quaterniond = Quaterniond(),
    scale: Vector3d = Vector3d(1.0),
) : Entity(EntityType.ITEM_DISPLAY, location) {
    var item = item
        set(value) {
            if (field == value) return
            updates.add(SetEntityDataPacket(id, EntityData.ITEM_STACK_ID(value.handle)))
            field = value
        }
    var rotation = rotation
        set(value) {
            if (field == value) return
            updates.add(SetEntityDataPacket(id, EntityData.LEFT_ROTATION_ID(value.toQuaternionf())))
            field = value
        }
    var scale = scale
        set(value) {
            if (field == value) return
            updates.add(SetEntityDataPacket(id, EntityData.SCALE_ID(scale.toVector3f())))
            field = value
        }
    override val extraPackets
        get() = listOf(SetEntityDataPacket(id,
            EntityData.ITEM_STACK_ID(item.handle),
            EntityData.LEFT_ROTATION_ID(rotation.toQuaternionf()),
            EntityData.SCALE_ID(scale.toVector3f()),
        ))
}

enum class Tag {world, rides, economy, events, frontend, backend, external, internal}
enum class Target(vararg val tags: Tag) {
    Minecraft(Tag.world, Tag.rides, Tag.economy, Tag.events),
    Hytale(Tag.world, Tag.rides, Tag.economy, Tag.events),
    Website(Tag.frontend, Tag.backend),
    AudioServer(Tag.frontend, Tag.backend),
    Discord(Tag.external, Tag.internal),
    //Connector? [Master server?]
}
sealed interface Role
data object Developer : Role
data object Builder : Role
data object Modeller : Role
data object Designer : Role
data object Marketeer : Role
data object Moderator : Role // Special: Default, only if no other role is present
data class Manager(val role: Role, val target: Target) : Role // Special: Contains another role and optional object/target

data class Crew(val roles: List<Role>) { constructor(vararg roles: Role): this(roles.toList()) }

val test = Crew(Manager(Developer, Target.Minecraft))

sealed class JsonException(message: String) : RuntimeException(message)
sealed class JsonReaderException(reader: JsonStringReader, message: String) : JsonException(
    buildString {
        append("$message\n")

        val lines = reader.str?.lines() ?: listOf()
        var currentIndex = 0
        var errorLineIndex = 0

        // Find the line where the error occurred
        for ((index, line) in lines.withIndex()) {
            if (currentIndex + line.length >= reader.next) {
                errorLineIndex = index
                break
            }
            currentIndex += line.length + 1 // +1 for the newline character
        }

        // Calculate the error's position in the specific line
        val errorPositionInLine = reader.next - currentIndex // Adjust to point to the end of the problematic token

        // Get two lines before and after the error line
        val startLine = (errorLineIndex - 2).coerceAtLeast(0)
        val endLine = (errorLineIndex + 2).coerceAtMost(lines.lastIndex)

        for (i in startLine..endLine) {
            val prefix = if (i == errorLineIndex) "> " else "  "
            append("$prefix Line ${i + 1}: ${lines[i]}\n")
            if (i == errorLineIndex) {
                append(" ".repeat(prefix.length + "Line ${i + 1}: ".length + errorPositionInLine) + "^\n")
            }
        }
    }.trim()
)


sealed class JsonFormatException(reader: JsonStringReader, message: String) : JsonReaderException(reader, message)
class JsonUnexpectedEndOfInputException(reader: JsonStringReader, message: String) : JsonFormatException(reader, message)
class JsonValueFormatException(reader: JsonStringReader, message: String) : JsonFormatException(reader, message)
class JsonObjectFormatException(reader: JsonStringReader, message: String) : JsonFormatException(reader, message)
class JsonArrayFormatException(reader: JsonStringReader, message: String) : JsonFormatException(reader, message)
class JsonBooleanFormatException(reader: JsonStringReader, message: String) : JsonFormatException(reader, message)
class JsonNullFormatException(reader: JsonStringReader, message: String) : JsonFormatException(reader, message)
class JsonNumberFormatException(reader: JsonStringReader, message: String) : JsonFormatException(reader, message)
class JsonStringFormatException(reader: JsonStringReader, message: String) : JsonFormatException(reader, message)

@Suppress("unused")
object Json {
    const val BEGIN_OBJECT = '\u007B'
    const val END_OBJECT = '\u007D'
    const val BEGIN_ARRAY = '\u005B'
    const val END_ARRAY = '\u005D'
    const val NAME_SEPARATOR = '\u003A'
    const val VALUE_SEPARATOR = '\u002C'
    const val ESCAPE = '\u005C'
    const val SPACE = '\u0020'
    const val HORIZONTAL_TAB = '\u0009'
    const val LINE_FEED = '\u000A'
    const val CARRIAGE_RETURN = '\u000D'
    const val DECIMAL_POINT = '\u002e'
    const val MINUS = '\u002D'
    const val PLUS = '\u002B'
    const val ZERO = '\u0030'
    const val E_LOWER = '\u0065'
    const val E_UPPER = '\u0045'
    const val QUOTATION_MARK = '\u0022'
    const val TRUE = "\u0074\u0072\u0075\u0065"
    const val FALSE = "\u0066\u0061\u006c\u0073\u0065"
    const val NULL = "\u006e\u0075\u006c\u006c"
    val WHITE_SPACE_CHARS = setOf(SPACE, HORIZONTAL_TAB, LINE_FEED, CARRIAGE_RETURN)
    val TRUE_CHARS = TRUE.toSet()
    val FALSE_CHARS = FALSE.toSet()
    val BOOLEAN_CHARS = TRUE_CHARS union FALSE_CHARS
    val NUMBER_RANGE = '0'..'9'
}

fun String.toJson() = JsonStringReader(this).parseValue()
fun JsonStringReader.parseValue(): Any? {
    return when(seek().peek()) {
        Json.BEGIN_OBJECT -> parseObject()
        Json.BEGIN_ARRAY -> parseArray()
        Json.QUOTATION_MARK -> parseString()
        Json.TRUE.first(), Json.FALSE.first() -> parseBoolean()
        Json.NULL.first() -> parseNull()
        in Json.NUMBER_RANGE, Json.MINUS, Json.DECIMAL_POINT -> parseNumber()
        else -> throw JsonValueFormatException(this, "Expected json value but got '${peek()}'")
    }
}

fun JsonStringReader.parseObject(): Map<String, Any?> {
    skip()
    val map = mutableMapOf<String, Any?>()
    while (true) {
        if (seek().peek() != '"') throw JsonObjectFormatException(this, "Expected object member name but got '${peek()}'")
        val name = parseString()
        takeUntil(Json.NAME_SEPARATOR)
        map[name] = parseValue()

        val char = seek().char()
        if (char == Json.END_OBJECT)
            break
        else if (char != Json.VALUE_SEPARATOR)
            throw JsonObjectFormatException(this, "Excepted value seperator '${Json.VALUE_SEPARATOR}' or end object '${Json.END_OBJECT}' but got '$char'")
    }
    return map
}

fun JsonStringReader.parseArray(): List<Any?> {
    skip()
    val list = mutableListOf<Any?>()
    while(true) {
        list.add(seek().parseValue())
        val char = seek().char()
        if (char == Json.END_ARRAY)
            break
        else if (char != Json.VALUE_SEPARATOR)
            throw JsonObjectFormatException(this, "Excepted value seperator '${Json.VALUE_SEPARATOR}' or end array '${Json.END_ARRAY}' but got '$char'")
    }
    return list
}

fun JsonStringReader.parseNumber() {
    println("found number")
}

fun JsonStringReader.parseBoolean(): Boolean {
    val bool = mark().takeOnly(*Json.BOOLEAN_CHARS.toCharArray()).joinToString("")
    if (bool !in listOf("true", "false")) throw JsonBooleanFormatException(apply{reset()}, "Boolean is not 'true' or 'false' but '$bool'")
    return bool.toBoolean()
}

fun JsonStringReader.parseNull(): Unit? {
    val value = mark().takeOnly(*Json.NULL.toCharArray()).joinToString("")
    if (value != "null") throw JsonNumberFormatException(apply{reset()}, "Null is not 'null' but '$value'")
    return null
}

fun JsonStringReader.parseString() = skip().takeUntil('"').joinToString("")
fun JsonStringReader.skip() = apply { skip(1) }
fun JsonStringReader.mark() = apply { mark(1) }
fun JsonStringReader.char() = read().toChar()
fun JsonStringReader.peek(): Char = mark().char().also { reset() }
fun JsonStringReader.seek() = apply {
    while (mark().char() in Json.WHITE_SPACE_CHARS) { /**/ }
    reset()
}
fun JsonStringReader.takeUntil(stop: Char) = mutableListOf<Char>().apply {
    var escape = false
    while(true) {
        val code = read()
        if (code == -1) throw JsonUnexpectedEndOfInputException(this@takeUntil, "Expected end character '$stop' before end of text.")
        if (code == stop.code && !escape) break
        add(code.toChar())
        escape = code.toChar() == Json.ESCAPE
    }
}
fun JsonStringReader.takeOnly(vararg include: Char) = mutableListOf<Char>().apply {
    while(true) {
        val code = read()
        if (code == -1 || code !in include.map(Char::code)) break
        add(code.toChar())
    }
}

fun main() {
    val arr = (0..10000000).toList()

    measureNanoTime {
        var a = 0
        for (i in arr) {
            a += i
        }
    }

    val total2 = (0..100).map {
        measureNanoTime {
            var a = 0
            for (i in arr) {
                a += i
            }
        }
    }.sum()

    val total3 = (0..100).map {
        measureNanoTime {
            var a = 0
            for (i in (0 until arr.size)) {
                a += arr[i]
            }
        }
    }.sum()

    val total1 = (0..100).map {
        measureNanoTime {
            var a = 0
            arr.forEach { a += it }
        }
    }.sum()

    println(total1)
    println(total2)
    println(total3)
}

@JvmInline
value class IntProvider(val value: Int)

fun testInt(block: IntProvider.() -> Unit) {

}

fun another() {
    testInt {
        value
    }
}
