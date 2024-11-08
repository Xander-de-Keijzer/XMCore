package nl.xandermarc.mc.lib.data

import org.bukkit.permissions.Permissible

enum class Ranks(val permission: String) {
    OWNER("rank.owner"),
    CREW("rank.crew");

    fun hasPermission(permissible: Permissible): Boolean = permissible.hasPermission(permission)
}
