package nl.xandermarc.mc.lib.sql

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import nl.xandermarc.mc.lib.logging.infoAll
import nl.xandermarc.mc.lib.logging.warn
import nl.xandermarc.mc.lib.logging.warnAll
import nl.xandermarc.mc.lib.sql.entities.SessionCoercion
import nl.xandermarc.mc.lib.sql.entities.User
import nl.xandermarc.mc.lib.sql.entities.UserSession
import nl.xandermarc.mc.lib.sql.tables.SessionCoercions
import nl.xandermarc.mc.lib.sql.tables.UserSessions
import nl.xandermarc.mc.lib.sql.tables.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

/*
Creates new user if not exists
 */
fun userJoined(uuid: UUID, username: String) {
    transaction {
        val user = User.findById(uuid)?.apply { this.username = username } ?: User.new(uuid) { this.username=username }
        UserSession.new { this.user=user }
    }
}

fun userQuit(uuid: UUID) = transaction {
    User.findById(uuid)?.let { user ->
        UserSession.find {
            (UserSessions.user eq user.id) and (UserSessions.quitAt.isNull())
        }.orderBy(UserSessions.joinedAt to SortOrder.DESC).firstOrNull()?.let { session ->
            session.quitAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        }
    }
}

fun coerceSessions() {
    transaction {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        UserSession.find {
            (UserSessions.quitAt.isNull())
        }.warnAll {
            "UserSession ${id.value} was coerced to quit=$now"
        }.forEach { session ->
            session.quitAt = now
            SessionCoercion.new { this.session = session; this.coercedTo = now }
        }
    }
}

fun testDatabase() {
    Database.connect("jdbc:h2:./h2db;", driver = "org.h2.Driver")

    transaction {
        SchemaUtils.create(Users)
        SchemaUtils.create(UserSessions)
        SchemaUtils.create(SessionCoercions)
    }

    val uuid1 = UUID.randomUUID()
    val uuid2 = UUID.randomUUID()

    userJoined(uuid1, "User1")
    userJoined(uuid2, "User2")
    userQuit(uuid1)
    userJoined(uuid1, "User3")

    coerceSessions()

    transaction {
        User.all().forEach {
            println(it.toString())
        }
        UserSession.all().forEach {
            println(it.toString())
        }
        SessionCoercion.all().forEach {
            println(it.toString())
        }
    }
}
