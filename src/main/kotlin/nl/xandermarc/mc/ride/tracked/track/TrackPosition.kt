package nl.xandermarc.mc.ride.tracked.track

data class TrackPosition(
    val track: Track,
    var trackSegment: TrackSegment,
    var position: Double,
    var direction: Boolean = true
) {
    fun move(distance: Double) {
        var remainingDistance = if (direction) distance else -distance

        while (remainingDistance != 0.0) {
            val segmentLength = trackSegment.path.length()
            val newPosition = position + remainingDistance

            if (newPosition > segmentLength) {
                remainingDistance = newPosition - segmentLength
                moveToNextSegment(forward = true)
            } else if (newPosition < 0) {
                remainingDistance = newPosition
                moveToNextSegment(forward = false)
            } else {
                position = newPosition
                remainingDistance = 0.0
            }
        }
    }

    private fun moveToNextSegment(forward: Boolean) {
        val nextSegmentId = if (forward) trackSegment.aConnected else trackSegment.bConnected ?: return
        val nextSegment = track.getSegment(nextSegmentId) ?: return
        val currentSegmentId = track.getSegmentId(trackSegment) ?: return
        val reversed = if (forward) {
            nextSegment.bConnected == currentSegmentId
        } else {
            nextSegment.aConnected == currentSegmentId
        }

        trackSegment = nextSegment
        direction = if (reversed) !forward else forward
        position = if (direction) 0.0 else trackSegment.path.length()
    }
}
