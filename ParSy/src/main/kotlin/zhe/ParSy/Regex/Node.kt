package zhe.ParSy.Regex

val kleeneInterval = Pair<UInt, UInt>(0.toUInt(), 0.toUInt())

class Node(
    val charset: Set<Char>,
    val interval: Pair<UInt, UInt>
) {
    fun isKleene(): Boolean {
        return this.interval == kleeneInterval
    }
}

fun dummyNode(): Node {
    return Node(setOf('a'), Pair(1.toUInt(), 1.toUInt()))
}
