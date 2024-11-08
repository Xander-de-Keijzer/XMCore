package nl.xandermarc.mc.ride.tracked

import nl.xandermarc.mc.ride.tracked.track.Track
import nl.xandermarc.mc.ride.tracked.track.TrackPosition

data class Train(
    val id: Int,
    var position: TrackPosition,
    var weight: Double
) {
    var speed: Double = 0.0
    class Builder(var weight: Double) {
        private var id: Int = -1
        private lateinit var track: Track
        private var segment: Int = 0
        private var position: Double = 0.0

        fun withId(id: Int) = apply { this.id = id }
        fun withTrack(track: Track) = apply { this.track = track }
        fun withSegment(segment: Int) = apply { this.segment = segment }
        fun withPosition(position: Double) = apply { this.position = position }

        fun build(): Train {
            val trackSegment = track.segments[segment]
                ?: throw IllegalArgumentException("Segment $segment not found in track ${track.name}")
            val trackPosition = TrackPosition(track, trackSegment, position)
            return Train(id, trackPosition, weight)
        }
    }
}
