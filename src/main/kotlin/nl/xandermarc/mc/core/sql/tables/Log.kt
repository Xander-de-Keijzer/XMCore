package nl.xandermarc.mc.core.sql.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object Log : UUIDTable("log") {
    val created = datetime("coerced_at").defaultExpression(CurrentDateTime)
    val levelName = varchar("level_name", 255)
    val levelWeight = integer("level_weight")
    val loggerName = varchar("logger_name", 255)
    val message = varchar("message", 1000)
}
