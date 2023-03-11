package zhe.ParSy.Regex

val kleeneInterval = Pair<UInt, UInt>(0.toUInt(), 0.toUInt())

class Node(
    charset: Set<Char>,
    interval: Pair<UInt, UInt>
) {
    var isTop: Boolean = false

    private var charsets: MutableList<Set<Char>> = mutableListOf<Set<Char>>(charset)
    private var interval: Pair<UInt, UInt> = interval

    fun getCharset() = charsets.reduce { acc, charset -> acc + charset }
    fun addCharset(charset: Set<Char>) {
        charsets += charset
    }
    fun getInterval() = interval

    fun incrementInterval() {
        interval = Pair(interval.first + 1.toUInt(), interval.second + 1.toUInt())
    }

    fun capInterval() {
        val max = (if (interval.first > interval.second) interval.first else interval.second).toUInt()
        interval = Pair(max, max)
    }

    fun isKleene(): Boolean {
        return this.interval == kleeneInterval
    }

    fun kleenize() {
        this.interval = kleeneInterval
    }

    fun joinCharset(other: Node): Set<Char> {
        return this.getCharset().union(other.getCharset())
    }

    fun joinInterval(other: Node): Pair<UInt, UInt> {
        var minLeft = this.getInterval().first
        if (other.getInterval().first < minLeft) {
            minLeft = other.getInterval().first
        }
        var maxRight = this.getInterval().second
        if (other.getInterval().second > maxRight) {
            maxRight = other.getInterval().second
        }
        return Pair(minLeft, maxRight)
    }

    override fun toString(): String {
        return "Node(charset=${getCharset()} interval=${getInterval()})"
    }

    override fun equals(other: Any?): Boolean = when (other) {
        is Node -> this.getCharset() == other.getCharset() && this.interval == other.interval
        else -> false
    }
}

fun dummyNode(): Node {
    return Node(setOf(), Pair(1.toUInt(), 1.toUInt()))
}

fun topNode(): Node {
    return dummyNode().apply { isTop = true }
}
