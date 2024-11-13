package nl.xandermarc.test

import nl.xandermarc.mc.lib.commands.Command
import nl.xandermarc.mc.lib.commands.annotations.Greedy
import nl.xandermarc.mc.lib.data.Globals
import nl.xandermarc.mc.lib.extensions.vec3d
import nl.xandermarc.mc.ride.Cart
import nl.xandermarc.mc.ride.Ride
import nl.xandermarc.mc.ride.Train
import nl.xandermarc.mc.ride.entity.Display
import nl.xandermarc.mc.ride.entity.Rideable
import kotlin.math.max
import kotlin.reflect.KClass
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberFunctions

object Tests {
    fun commandArguments(command: Command) {
        command::class.memberFunctions.filter { it.parameters.size > 1 }.forEach { func ->
            if (func.parameters.dropLast(1).any {
                    it.type.classifier == List::class || it.hasAnnotation<Greedy>()
                }) {
                Globals.logger.warning("Function '${func.name}(${
                    func.parameters.drop(1).joinToString(", ") { "${it.name}: ${(it.type.classifier as KClass<*>).simpleName}" }
                })' of command '${command::class.simpleName}' has an argument of type list or greedy string\n" +
                        "which will take all remaining arguments from the stack, but has more arguments defined after making the function unsatisfiable.")
            }
        }
    }
}

class Json()

class JsonBuilder {
    fun obj(builder: JsonBuilder.() -> Pair<String, JsonObject>): JsonObject {
        println(builder())
        return builder().second
    }
    fun build(): Json {
        return Json()
    }
}

abstract class JsonObject
class StringJsonObject : JsonObject()

fun json(json: JsonBuilder.() -> Unit): Json {
    val builder = JsonBuilder()
    builder.json()
    return builder.build()
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
