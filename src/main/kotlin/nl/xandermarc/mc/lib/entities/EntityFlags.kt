package nl.xandermarc.mc.lib.entities

data class EntityFlags(
    var onFire: Boolean = false,
    var sneaking: Boolean = false,
    var sprinting: Boolean = false,
    var swimming: Boolean = false,
    var invisible: Boolean = false,
    var glowing: Boolean = false,
) {
    constructor(int: Int) : this(
        onFire=(int and (1 shl 0)) == 1,
        sneaking=(int and (1 shl 1)) == 1,
        sprinting=(int and (1 shl 3)) == 1,
        swimming=(int and (1 shl 4)) == 1,
        invisible=(int and (1 shl 5)) == 1,
        glowing=(int and (1 shl 6)) == 1,
    )
    constructor(byte: Byte) : this(byte.toInt())
    val byte get() = run {
        var byte = 0
        if (onFire) byte = byte or (1 shl 0)
        if (sneaking) byte = byte or (1 shl 1)
        if (sprinting) byte = byte or (1 shl 3)
        if (swimming) byte = byte or (1 shl 4)
        if (invisible) byte = byte or (1 shl 5)
        if (glowing) byte = byte or (1 shl 6)
        byte.toByte()
    }
}
