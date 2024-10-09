package nl.xandermarc.mc.lib.async

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

object Scopes {
    val default = CoroutineScope(Dispatchers.Default)
    val io = CoroutineScope(Dispatchers.IO)
}