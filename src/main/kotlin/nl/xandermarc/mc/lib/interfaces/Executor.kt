package nl.xandermarc.mc.lib.interfaces

import org.bukkit.plugin.java.JavaPlugin

interface Executor {
    fun register(plugin: JavaPlugin): Executor
}
