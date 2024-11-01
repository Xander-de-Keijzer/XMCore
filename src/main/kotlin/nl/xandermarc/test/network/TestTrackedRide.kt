package nl.xandermarc.test.network

import nl.xandermarc.mc.lib.area.CuboidArea
import nl.xandermarc.mc.ride.tracked.TrackedRide
import nl.xandermarc.mc.ride.tracked.Train
import org.joml.Vector3d

object TestTrackedRide : TrackedRide<CuboidArea>(
    "test",
    CuboidArea(Vector3d(), Vector3d())
) { // Define carts sizes models etc
    override suspend fun loadAsync() {
        loadTrack("test_track")
        loadTrack("another_track")
    }

    override fun load() {
        val train = Train.Builder(weight = 500.0)
        track("test_track") {
            addTrain(train, segment = segments.keys.random(), position = 20.0) //ID: 1
            addTrain(train, segment = segments.keys.random(), position = 30.0) //ID: 2
        }
        track("another_track") {
            addTrain(train, segment = segments.keys.random(), position = 660.0) //ID: 3
            addTrain(train, segment = segments.keys.random(), position = 80.0) //ID: 4
            addTrain(train, segment = segments.keys.random(), position = 80.0) { //ID: 5 (context=Train.Builder)
                weight = 600.0 // Change weight
            }
        }
    }
}
