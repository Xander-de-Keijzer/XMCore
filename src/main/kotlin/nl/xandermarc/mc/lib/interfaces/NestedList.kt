package nl.xandermarc.mc.lib.interfaces

fun interface NestedList<T> {
    fun nested(): Iterable<T>
}
