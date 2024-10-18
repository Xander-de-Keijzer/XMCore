package nl.xandermarc.mc.core.sql.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object UserSessions : IntIdTable("user_sessions") {
    val user = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
    val joinedAt = datetime("joined_at").defaultExpression(CurrentDateTime)
    val quitAt = datetime("quit_at").nullable().default(null)
}
