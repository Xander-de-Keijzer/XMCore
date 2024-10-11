package nl.xandermarc.mc.rides.objects

import nl.xandermarc.mc.lib.logging.info
import nl.xandermarc.mc.rides.tracked.TrackedRide
import nl.xandermarc.mc.rides.tracked.Train

object TestTrackedRide : TrackedRide("test") { // Define carts sizes models etc
    override fun initAsync() {
        info("Init async $name")
        Thread.sleep(1500)
        loadTrack("test_track")
        loadTrack("another_track")
        info("Init async done $name")
    }

    override fun init() {
        info("Init sync $name")
        val train = Train.Builder(weight = 500.0)
        track("test_track") {
            addTrain(train, segment = 1, position = 20.0) //ID: 1
            addTrain(train, segment = 2, position = 30.0) //ID: 2
        }
        track("another_track") {
            addTrain(train, segment = 3, position = 660.0) //ID: 3
            addTrain(train, segment = 4, position = 80.0) //ID: 4
            addTrain(train, segment = 5, position = 80.0) { //ID: 5 (context=Train.Builder)
                weight = 600.0 // Change weight
            }
        }
        info("Init sync done $name")
    }
}
