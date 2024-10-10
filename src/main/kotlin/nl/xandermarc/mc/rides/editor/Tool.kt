package nl.xandermarc.mc.rides.editor

class Tool<T>(
    val onRightClick: Event<T> = Event(null) {},
    val onLeftClick: Event<T> = Event(null) {},
    val onClick: Event<T> = Event(null) {}
) {
    class Event<T>(val name: String?, private val action: T.() -> Unit) {
        fun invoke(editor: T) = action.invoke(editor)
    }
}
