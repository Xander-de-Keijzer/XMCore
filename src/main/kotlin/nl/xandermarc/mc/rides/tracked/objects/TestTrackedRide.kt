package nl.xandermarc.mc.rides.tracked.objects

import nl.xandermarc.mc.rides.tracked.TrackedRide
import nl.xandermarc.mc.rides.tracked.Train

object TestTrackedRide : TrackedRide("test") { // Define carts sizes models etc
    override fun init() {
        val train = Train.Builder(weight = 500.0)
        addTrack("test_track") {
            addTrain(train, segment = 1, position = 20.0) //ID: 1
            addTrain(train, segment = 2, position = 30.0) //ID: 2
        }
        addTrack("another_track") {
            addTrain(train, segment = 3, position = 660.0) //ID: 3
            addTrain(train, segment = 4, position = 80.0) //ID: 4
            addTrain(train, segment = 5, position = 80.0) { //ID: 5 (context=Train.Builder)
                weight = 600.0 // Change weight
            }
        }
    }
}