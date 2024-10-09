package nl.xandermarc.mc.lib.async

import kotlinx.coroutines.*

inline fun launch(crossinline task: CoroutineScope.() -> Unit): Task =
    Task(Scopes.default.launch{ task() })
