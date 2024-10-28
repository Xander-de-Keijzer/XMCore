package nl.xandermarc.mc.lib.data

import nl.xandermarc.mc.core.XMC
import java.nio.file.Path

object Paths : Path by XMC.instance.dataPath {
    object Rides : Path by Paths.resolve("rides")
    object Tracks : Path by Paths.resolve("tracks")
}
