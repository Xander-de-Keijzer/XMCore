package nl.xandermarc.mc.ride

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import nl.xandermarc.mc.core.managers.TrackManager
import nl.xandermarc.mc.lib.Math.encode
import nl.xandermarc.mc.lib.extensions.launchAsync

abstract class Ride(
    val name: String
) {
    internal val tracks = mutableMapOf<String, Track>()
    internal val trains = mutableListOf<Train>()

    private val mutex = Mutex()
    private fun launch(action: suspend () -> Unit) = launchAsync {
        mutex.withLock {
            check(state != State.DISABLING && state != State.ENABLING) {
                "Ride is stuck in transitioning state of ${state.name}"
            }
            action()
        }
    }

    fun enable() = launch {
        if (state != State.DISABLED) return@launch
        state = State.ENABLING
        load()
        state = State.ENABLED
        println("Enabled")
    }

    fun disable() = launch {
        if (state != State.ENABLED) return@launch
        state = State.DISABLING
        unload()
        state = State.DISABLED
        println("Disabled")
    }

    fun reset() = launch {
        if (state == State.ENABLED) {
            state = State.DISABLING
            unload()
            state = State.DISABLED
        }
        state = State.ENABLING
        load()
        state = State.ENABLED
        println("Reset")
    }

    private fun unload() {
        trains.forEach { it.remove() }
        remove()
    }

    protected abstract fun load()
    protected abstract fun remove()

    /*
    Track
     */
    abstract class Controller {
        var segment: Track.Segment? = null
            set(value) { field?.controller = null; field = value; }
        open fun onTrainTick(train: Train) {}
        open fun onCartTick(cart: Cart) {}
    }

    protected fun loadTrack(
        name: String,
        block: Track.() -> Unit = {}
    ) {
        val track = TrackManager.getOrCreate(name) ?:
            throw IllegalStateException("Track $name does not exist.")
        tracks[name] = track
        block(track)
    }
    protected fun Track.addTrain(
        trainBuilder: Train.Builder,
        segment: Int,
        position: Double,
        direction: Boolean = true,
    ) = trains.add(trainBuilder.build(position(Track.Segment(listOf()), position, direction)))
    protected fun Track.segment(a: Int, b: Int) = segment(encode(a, b))
    protected fun Track.segment(segment: Int) = Track.Segment(listOf())
    protected fun Track.Segment.track(block: Track.(Track.Segment) -> Unit) {
        tracks.values.find { it.segments.containsValue(this) }?.block(this)
    }

    var state: State = State.DISABLED
    enum class State {
        DISABLED,
        ENABLING,
        ENABLED,
        DISABLING
    }
}
