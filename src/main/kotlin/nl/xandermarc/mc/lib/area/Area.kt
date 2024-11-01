package nl.xandermarc.mc.lib.area

import nl.xandermarc.mc.lib.data.Globals
import nl.xandermarc.mc.lib.extensions.toLocation
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import org.joml.Vector2d
import org.joml.Vector3d
import org.joml.Vector3f

abstract class Area(private val _world: World? = null) {
    private val world get() = _world ?: Globals.world
    abstract val min: Vector3d
    abstract val max: Vector3d
    abstract fun contains(x: Double, y: Double, z: Double): Boolean
    operator fun contains(location: Location) = location.world == world && contains(location.x, location.y, location.z)
    operator fun contains(vector: Vector) = contains(vector.x, vector.y, vector.z)
    operator fun contains(vector: Vector3d) = contains(vector.x, vector.y, vector.z)
    operator fun contains(vector: Vector3f) = contains(Vector3d(vector))
    operator fun contains(player: Player) = contains(player.location)
    operator fun contains(entity: Entity) = contains(entity.location)
    open operator fun contains(vector: Vector2d) = contains(vector.x, 0.0, vector.y)

    val minChunk get() = min.toLocation(world).chunk
    val maxChunk get() = max.toLocation(world).chunk
    val chunks get() = chunkCoords.map { world.getChunkAt(it.first, it.second) }
    open val chunkCoords get() =
        (minChunk.x..maxChunk.x).map { x ->
            (minChunk.z..maxChunk.z).map { z ->
                Pair(x, z)
            }
        }.flatten()

    fun loadChunks(keepLoaded: Boolean = false) {
        chunkCoords.forEach {
            if (keepLoaded) world.addPluginChunkTicket(it.first, it.second, Globals.instance)
            else world.loadChunk(it.first, it.second)
        }
    }

    fun releaseChunks() {
        chunkCoords.forEach {
            world.removePluginChunkTicket(it.first, it.second, Globals.instance)
        }
    }

    override fun toString(): String {
        return "Area(world=$world, min=$min, max=$max, chunks=${chunkCoords.size})"
    }
}
