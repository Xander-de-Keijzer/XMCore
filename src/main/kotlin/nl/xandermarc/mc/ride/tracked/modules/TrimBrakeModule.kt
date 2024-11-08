package nl.xandermarc.mc.ride.tracked.modules

import nl.xandermarc.mc.ride.tracked.Train
import nl.xandermarc.mc.ride.tracked.listeners.TrainListener

data class TrimBrakeModule(
    val targetSpeed: Double,
    val brakeForce: Double
) : TrainListener {
    override fun onTrainTick(train: Train) {
        if (train.speed > targetSpeed) {
            train.speed -= brakeForce
            if (train.speed < targetSpeed) {
                train.speed = targetSpeed
            }
        }
    }
}
