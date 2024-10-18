package nl.xandermarc.mc.core.sql.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object SessionCoercions : IntIdTable("session_coercions") {
    val session = reference("session_id", UserSessions, onDelete = ReferenceOption.CASCADE)
    val coercedAt = datetime("coerced_at").defaultExpression(CurrentDateTime)
    val coercedTo = datetime("coerced_to").defaultExpression(CurrentDateTime)
}
