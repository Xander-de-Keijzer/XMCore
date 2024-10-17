package nl.xandermarc.mc.lib.sql.entities

import nl.xandermarc.mc.lib.sql.tables.SessionCoercions
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class SessionCoercion(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SessionCoercion>(SessionCoercions)

    var session by UserSession referencedOn SessionCoercions.session
    private val coercedAt by SessionCoercions.coercedAt
    var coercedTo by SessionCoercions.coercedTo

    override fun toString() =
        "SessionCoercion(id=$id, session=${session.id}, coercedAt=$coercedAt, coercedTo=$coercedTo)"
}
