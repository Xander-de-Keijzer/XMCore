package nl.xandermarc.mc.lib.data

import java.nio.file.Path

object Paths : Path by Globals.instance.dataPath {
    object Rides : Path by resolve("rides")
    object Tracks : Path by resolve("tracks")
}
