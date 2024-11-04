package nl.xandermarc.mc.lib.utils

import org.bukkit.plugin.java.JavaPlugin

interface Manager {
    fun enable(plugin: JavaPlugin)
    fun disable()
}
