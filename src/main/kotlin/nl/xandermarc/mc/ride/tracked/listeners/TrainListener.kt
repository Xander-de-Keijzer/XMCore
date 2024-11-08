package nl.xandermarc.mc.ride.tracked.listeners

import nl.xandermarc.mc.ride.tracked.Train

fun interface TrainListener {
    fun onTrainTick(train: Train)
}
