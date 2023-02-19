package zhe.ParSy.Regex

val kleeneInterval = Pair<UInt, UInt>(0.toUInt(), 0.toUInt())

class Node(
    val charset: Set<Char>,
    val interval: Pair<UInt, UInt>,
) {
    var isTop: Boolean = false

    fun isKleene(): Boolean {
        return this.interval == kleeneInterval
    }

    override fun toString(): String {
        return "Node(charset=${charset} interval=${interval})"
    }

    override fun equals(other: Any?): Boolean = when (other) {
        is Node -> this.charset == other.charset && this.interval == other.interval
        else -> false
    }
}

fun dummyNode(): Node {
    return Node(setOf(), Pair(1.toUInt(), 1.toUInt()))
}
