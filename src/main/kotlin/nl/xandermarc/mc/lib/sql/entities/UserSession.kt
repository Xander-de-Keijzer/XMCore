package nl.xandermarc.mc.lib.sql.entities

import nl.xandermarc.mc.lib.sql.tables.UserSessions
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserSession(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserSession>(UserSessions)

    var user by User referencedOn UserSessions.user
    private val joinedAt by UserSessions.joinedAt
    var quitAt by UserSessions.quitAt

    override fun toString() = "UserSession(id=${id.value}, user=${user.id.value}, joined=$joinedAt, quit=$quitAt)"
}
