package nl.xandermarc.mc.core.sql.entities

import nl.xandermarc.mc.core.sql.tables.Log
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class LogEntry(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<LogEntry>(Log)

    var created by Log.created
    var levelName by Log.levelName
    var levelWeight by Log.levelWeight
    var loggerName by Log.loggerName
    var message by Log.message

    override fun toString() = "[$created $levelName($levelWeight)]($loggerName): $message"
}