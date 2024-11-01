package nl.xandermarc.mc.test

import nl.xandermarc.mc.lib.area.CuboidArea
import nl.xandermarc.mc.lib.commands.Command
import nl.xandermarc.mc.lib.commands.annotations.Options
import nl.xandermarc.mc.lib.commands.annotations.PlayerOnly
import nl.xandermarc.mc.lib.commands.annotations.Range
import nl.xandermarc.mc.lib.commands.annotations.Root
import nl.xandermarc.mc.lib.extensions.vec3d
import nl.xandermarc.mc.ride.tracked.TrackedRide
import org.joml.Vector3d

private sealed class RideObject(val offset: Vector3d)

private class Rideable(offset: Vector3d) : RideObject(offset)
private class Display(val model: Int, offset: Vector3d = Vector3d()) : RideObject(offset)

private fun interface CartList {
    fun carts(): Iterable<Cart>
}

private class Cart(
    vararg objects: RideObject,
) : CartList {
    override fun carts() = listOf(this)
    fun copy(n: Int) = CartList { List(n) { this@Cart } }
}

private class Train(
    vararg entries: CartList
)

private fun addTrain(train: Train, segment: Int, position: Double) {}

fun test() {

    object : TrackedRide<CuboidArea>("example", CuboidArea(Vector3d(), Vector3d())) {
        override suspend fun loadAsync() {
            loadTrack("example_track")
        }

        override fun load() {
            val train = Train(
                Cart(
                    Display(1)
                ),
                Cart(
                    Display(2),
                    Rideable(vec3d(x=1.0, z=0.5)), //FL
                    Rideable(vec3d(x=-1.0, z=0.5)), //FR
                    Rideable(vec3d(x=1.0, z=-0.5)), //BL
                    Rideable(vec3d(x=-1.0, z=-0.5)), //BR
                ).copy(5)
            )

            track("example_track") {
                addTrain(train, 3, 20.0)
                addTrain(train, 5, 15.0)
                addTrain(train, 7, 33.0)
            }
        }

    }

}

fun test2(vararg test: Any) {
    fun receive(f: (Array<out Any>) -> Unit) {

    }
    receive {

    }
}


object TestCommand : Command(description = "Testing command") {

    @Root
    fun help() {
        println("/track")
    }

    fun list() {
        println("/track list")
    }

    fun create(@Range(max=10) name: Int) {
        println("/track create $name")
    }

    fun remove(@Options("test", "another") name: String) {
        println("/track remove $name")
    }

    @PlayerOnly
    fun edit(name: String) {
        println("$player")
        println("/track edit $name")
    }

    fun ride(edit: Unit, test: String) {
        println("/track ride edit $test")
    }
}
