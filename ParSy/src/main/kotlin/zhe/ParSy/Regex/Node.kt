package zhe.ParSy.Regex

val kleeneInterval = Pair<UInt, UInt>(0.toUInt(), 0.toUInt())

class Node(
    charset: Set<Char>,
    interval: Pair<UInt, UInt>,
) {
    var isTop: Boolean = false

    private var charset: Set<Char> = charset
    private var interval: Pair<UInt, UInt> = interval

    fun getCharset() = charset
    fun getInterval() = interval

    fun isKleene(): Boolean {
        return this.interval == kleeneInterval
    }

    fun widenUpper(widening: Int) = this.let {
        interval = Pair(
            this.interval.first,
            this.interval.second + widening.toUInt(),
        )
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
