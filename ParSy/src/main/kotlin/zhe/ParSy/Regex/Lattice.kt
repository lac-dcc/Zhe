package zhe.ParSy.Regex

class Lattice(
    private val nf: NodeFactory,
    private val baseNodes: List<Node>
) {
    init {
    }

    fun meet(n1: Node, n2: Node): Node {
        var minLeft = n1.interval.first
        if (n2.interval.first < minLeft) {
            minLeft = n2.interval.first
        }
        var maxRight = n1.interval.second
        if (n2.interval.second > maxRight) {
            maxRight = n2.interval.second
        }
        return Node(
            n1.charset.union(n2.charset),
            Pair(minLeft, maxRight)
        )
    }
}
