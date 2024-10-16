package nl.xandermarc.mc.lib.entities

import kotlin.random.Random

object EntityID {
    private val random = Random(System.currentTimeMillis())
    private var serial = 0
    val next: Int get() = random.nextInt()
    val nextSerial: Int get() = serial++
}
