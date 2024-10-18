package nl.xandermarc.mc.core.sql.entities

import nl.xandermarc.mc.core.sql.tables.UserSessions
import nl.xandermarc.mc.core.sql.tables.Users
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class User(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<User>(Users)

    var username by Users.username
    val sessions by UserSession referrersOn UserSessions.user

    override fun toString(): String {
        return "User(uuid=${id.value}, username=$username)"
    }
}
