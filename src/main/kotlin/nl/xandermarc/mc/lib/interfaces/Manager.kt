package nl.xandermarc.mc.lib.interfaces

import org.bukkit.plugin.java.JavaPlugin

interface Manager {
    fun enable(plugin: JavaPlugin): Manager
    fun disable(): Manager
}
