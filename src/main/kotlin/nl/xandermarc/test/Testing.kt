package nl.xandermarc.test

import nl.xandermarc.mc.lib.commands.Command
import nl.xandermarc.mc.lib.commands.annotations.*

fun test() {

}


@Permission("You are not allowed to use this command.")
object TestCommand : Command(description = "Testing command") {

    @Root
    fun help() {
        println("/test")
    }

    fun list() {
        println("/test list")
    }

    fun create(@Range(max=10) name: Int) {
        println("/test create $name")
    }

    fun remove(@Options("test", "another") name: String) {
        println("/test remove $name")
    }

    @PlayerOnly
    fun edit(name: String) {
        println("$player")
        println("/test edit $name")
    }

    fun ride(edit: Unit, @Greedy("_") test: String) {
        println("/test ride edit $test")
    }

    fun all(error: String) {
        println("/test all $error")
    }

    fun status(status: Status) {
        println("/test status $status")
    }

    enum class Status {
        ON,
        OFF
    }
}
