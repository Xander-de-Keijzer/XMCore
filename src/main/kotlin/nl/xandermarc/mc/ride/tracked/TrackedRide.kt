package nl.xandermarc.mc.ride.tracked

import kotlinx.coroutines.ensureActive
import nl.xandermarc.mc.lib.extensions.debugAll
import nl.xandermarc.mc.lib.extensions.info
import nl.xandermarc.mc.ride.Ride
import nl.xandermarc.mc.ride.tracked.track.Track
import nl.xandermarc.mc.ride.tracked.track.TrackManager
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.coroutineContext

abstract class TrackedRide(rideName: String) : Ride(rideName) {
    private val nextTrainId = AtomicInteger(1)
    private val tracks = mutableListOf<Track>()
    val trackList
        get() = tracks.toList()
    private val trains = mutableListOf<Train>()
    val trainList
        get() = trains

    protected suspend fun loadTrack(
        trackName: String,
        builder: Track.() -> Unit = {}
    ) {
        coroutineContext.ensureActive()
        val track = TrackManager.getOrCreate(trackName)
        if (track.segments.isEmpty()) track.loadOrGenerateSegments()
        track.apply { builder() }
        tracks.add(track)
    }

    protected fun Track.addTrain(
        train: Train.Builder,
        segment: Int,
        position: Double,
        builder: Train.Builder.() -> Unit = {}
    ) {
        trains.add(train.apply {
            withId(nextTrainId.getAndIncrement())
            withTrack(this@addTrain)
            withSegment(segment)
            withPosition(position)
            builder()
        }.build())
    }

    protected fun train(id: Int) = trains.find { it.id == id }

    protected inline fun train(id: Int, f: (Train) -> Unit) {
        train(id)?.let { f(it) }
    }

    protected fun trains(trackName: String) = trains.filter { it.position.track.name == trackName }

    protected fun track(name: String) = tracks.find { it.name == name }

    protected inline fun track(name: String, f: Track.() -> Unit) {
        track(name)?.let { f(it) }
    }

    protected fun Track.segment(id: Int) = segments[id]

    override fun update() {
        trains.debugAll { "Sync: $this" }
    }

    override suspend fun updateAsync() {
        trains.debugAll { "Async: $this" }
    }

    override fun remove() {
        info("Removing tracked ride $name...")
        trains.debugAll { "Removed: ${this.id}" }
        nextTrainId.set(1)
        trains.clear()
        tracks.clear()
        //TODO("Remove train instanced")
        info("Removed tracked ride $name.")
    }
}