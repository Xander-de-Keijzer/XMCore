package nl.xandermarc.mc.core.sql.tables

import org.jetbrains.exposed.dao.id.UUIDTable

object Users : UUIDTable("users") {
    val username = varchar("username", 16)
}
