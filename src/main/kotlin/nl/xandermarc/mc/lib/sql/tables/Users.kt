package nl.xandermarc.mc.lib.sql.tables

import org.jetbrains.exposed.dao.id.UUIDTable

object Users : UUIDTable("users") {
    val username = varchar("username", 16)
}
