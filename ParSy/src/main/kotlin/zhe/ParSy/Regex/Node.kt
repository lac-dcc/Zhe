package zhe.ParSy.Regex

data class Node(
    val rule: String,
    val parents: Set<Node>,
    val level: Int,
)

fun dummyNode(): Node {
    return Node("", setOf(), 0)
}
