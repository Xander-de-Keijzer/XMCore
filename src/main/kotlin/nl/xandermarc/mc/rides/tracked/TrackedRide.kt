package nl.xandermarc.mc.rides.tracked

import nl.xandermarc.mc.lib.logging.debugAll
import nl.xandermarc.mc.rides.Ride
import java.util.concurrent.atomic.AtomicInteger

abstract class TrackedRide(rideName: String) : Ride(rideName) {
    private val nextTrainId = AtomicInteger(1)
    protected val tracks = arrayListOf<Track>()
    protected val trains = arrayListOf<Train>()

    protected fun addTrack(
        trackName: String,
        builder: Track.() -> Unit
    ) {
        tracks.add(
            TrackManager.getOrCreate(trackName)
                .loadOrGenerateSegments()
                .apply(builder)
        )
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

    protected inline fun track(name: String, f: (Track) -> Unit) {
        track(name)?.let { f(it) }
    }

    override fun update() {
        trains.debugAll { "Sync: $this" }
    }

    override fun updateAsync() {
        trains.debugAll { "Async: $this" }
    }

    override fun remove() {
        trains.debugAll { "Removed: $this" }
        nextTrainId.set(1)
        trains.clear()
        tracks.clear()
        //TODO("Remove train instanced")
    }
}