package nl.xandermarc.test

import nl.xandermarc.mc.lib.area.CuboidArea
import nl.xandermarc.mc.lib.commands.Command
import nl.xandermarc.mc.lib.commands.annotations.*
import nl.xandermarc.mc.lib.data.Ranks
import nl.xandermarc.mc.lib.extensions.vec3d
import nl.xandermarc.mc.lib.packets.BundlePacket
import nl.xandermarc.mc.lib.packets.Packet
import nl.xandermarc.mc.ride.tracked.TrackedRide
import nl.xandermarc.mc.ride.tracked.track.Track
import org.joml.Vector3d

private sealed class RideObject {
    abstract val offset: Vector3d
    private val children = mutableListOf<RideObject>()
    fun withChildren(vararg children: RideObject): RideObject {
        this.children.addAll(children)
        return this
    }
    fun create(): Packet<*> {
        val packets = mutableListOf(spawn())
        packets.addAll(children.map { it.spawn() })
        return BundlePacket(packets)
    }
    protected abstract fun spawn(): Packet<*>
}

private data class Rideable(
    override val offset: Vector3d
) : RideObject() {
    override fun spawn(): Packet<*> {
        TODO("Not yet implemented")
    }
}
private data class Display(
    override val offset: Vector3d,
    val model: Int
) : RideObject() {
    override fun spawn(): Packet<*> {
        TODO("Not yet implemented")
    }
}

private fun interface CartList {
    fun carts(): Iterable<Cart>
}

private data class Cart(
    val objects: List<RideObject>,
) : CartList {
    constructor(vararg objects: RideObject) : this(listOf(*objects))
    override fun carts() = listOf(this)
    fun copy(n: Int) = CartList { List(n) { this@Cart.copy() } }
}

private data class Train(
    val entries: List<CartList>
) {
    constructor(vararg entries: CartList) : this(listOf(*entries))
}

private fun Track.addTrain(train: Train, segment: Int, position: Double) {

}

fun test() {

    object : TrackedRide("example", CuboidArea(Vector3d(), Vector3d())) {
        override suspend fun loadAsync() {
            loadTrack("example_track")
        }

        override fun load() {
            val train = Train(
                Cart(
                    Display(vec3d(), 1)
                ),
                Cart(
                    Display(vec3d(), 2),
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

@Permission("You are not allowed to use this command.")
object TestCommand : Command(description = "Testing command") {

    @Root
    fun help() {
        println("/test")
    }

    @RankPermission(Ranks.CREW, "This command can only be executed by crew.")
    fun list() {
        println("/test list")
    }

    fun create(@Range(max=10) name: Int) {
        println("/test create $name")
    }

    fun remove(@Options("test", "another") name: String) {
        println("/test remove $name")
    }

    @PlayerOnly
    fun edit(name: String) {
        println("$player")
        println("/test edit $name")
    }

    fun ride(edit: Unit, @Greedy("_") test: String) {
        println("/test ride edit $test")
    }

    fun all(error: String) {
        println("/test all $error")
    }

    fun status(status: Status) {
        println("/test status $status")
    }

    enum class Status {
        ON,
        OFF
    }
}
