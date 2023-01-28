package zhe.ParSy.Regex

data class Node(
    val charset: Set<Char>,
    val interval: Pair<UInt, UInt>
)

fun dummyNode(): Node {
    return Node(setOf('a'), Pair(1.toUInt(), 1.toUInt()))
}
