package nl.xandermarc.mc.core.sql

import nl.xandermarc.mc.lib.data.Globals
import nl.xandermarc.mc.lib.extensions.launchAsync
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

fun transaction(statement: Transaction.() -> Unit) {
    org.jetbrains.exposed.sql.transactions.transaction(
        db=Globals.internalDB,
        statement=statement
    )
    // TODO Implement external database
//    launchAsync("exposed_suspend_transaction") {
//        newSuspendedTransaction(
//            context = coroutineContext,
//            db = Globals.externalDB,
//            statement = statement
//        )
//    }
}

fun <T> asyncTransaction(statement: suspend Transaction.() -> T) {
    statement.launchAsync("exposed_suspend_transaction") {
        newSuspendedTransaction(
            context=coroutineContext,
            db=Globals.internalDB,
            statement=statement
        )
        // TODO Implement external database
//        newSuspendedTransaction(
//            context=coroutineContext,
//            db=Globals.externalDB,
//            statement=statement
//        )
    }
}

fun createTables(vararg tables: Table) = transaction {
    SchemaUtils.create(*tables)
}
