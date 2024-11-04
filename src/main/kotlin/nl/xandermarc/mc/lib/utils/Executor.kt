package nl.xandermarc.mc.lib.utils

import org.bukkit.plugin.java.JavaPlugin

interface Executor {
    fun register(plugin: JavaPlugin)
}
