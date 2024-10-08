package nl.xandermarc.mc.rides.editor

class Tool<T>(
    val onRightClick: Pair<String, T.() -> Unit>? = null,
    val onLeftClick: Pair<String, T.() -> Unit>? = null,
    val onClick: Pair<String, T.() -> Unit>? = null
)