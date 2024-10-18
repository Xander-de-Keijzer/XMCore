package nl.xandermarc.mc.lib.data

import kotlin.random.Random

object EntityID {
    private val random = Random(System.currentTimeMillis())
    val next: Int get() = random.nextInt()
}
