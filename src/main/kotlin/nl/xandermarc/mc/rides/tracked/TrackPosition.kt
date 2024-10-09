package nl.xandermarc.mc.rides.tracked

data class TrackPosition(
    var track: Track,
    var segment: TrackSegment,
    var position: Double
)
