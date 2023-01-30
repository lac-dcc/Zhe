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
}

fun dummyNode(): Node {
    return Node(setOf(), Pair(1.toUInt(), 1.toUInt()))
}
