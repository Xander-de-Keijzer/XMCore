package nl.xandermarc.test

import nl.xandermarc.mc.ride.Ride
import nl.xandermarc.mc.ride.Train

object TestTrackedRide : Ride(
    "test"
) {

    override fun load() {
        val train = Train.Builder(

        )
        loadTrack("test_track") {
            addTrain(train, segment = segments.keys.random(), position = 20.0) //ID: 1
            addTrain(train, segment = segments.keys.random(), position = 30.0) //ID: 2
        }
        loadTrack("another_track") {
            addTrain(train, segment = segments.keys.random(), position = 660.0) //ID: 3
            addTrain(train, segment = segments.keys.random(), position = 80.0) //ID: 4
            addTrain(train, segment = segments.keys.random(), position = 80.0)
        }
    }

    override fun remove() {
        TODO("Not yet implemented")
    }
}
