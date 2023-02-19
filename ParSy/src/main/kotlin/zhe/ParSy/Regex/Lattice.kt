package zhe.ParSy.Regex

class PowersetLattice(
    private val baseNodes: List<Node>,
    private val disjointBaseNodes: List<Pair<Node, Node>>,
) {
    public val top = dummyNode().apply { this.isTop = true }

    private fun areDisjoint(n1: Node, n2: Node): Boolean {
        disjointBaseNodes.forEach {
            if (it.first == n1 && it.second == n2) {
                    return true
            } else if (it.second == n1 && it.first == n2) {
                return true
            }
        }
        return false
    }

    fun meet(n1: Node, n2: Node): Node {
        if (areDisjoint(n1, n2)) {
            return top
        }
        return Node(
            n1.getCharset().union(n2.getCharset()),
            kleeneInterval
        )
    }
}

class Lattice(
    private val baseNodes: List<Node>,
    // TODO: improve disjoint base nodes treatment
    private val disjointBaseNodes: List<Pair<Node, Node>>,
) {
    private val powersetLattice = PowersetLattice(baseNodes, disjointBaseNodes)

    // TODO: Remove top attribute from this class
    private val top = powersetLattice.top

    private val baseNodesMap = mutableMapOf<Char, Node>()
    init {
        // Assume the charsets of the base nodes are disjoint
        //
        // TODO: is there a way to avoid this assumption?
        baseNodes.forEach { node ->
            node.getCharset().forEach { c ->
                baseNodesMap[c] = node
            }
        }
    }

    private fun getParentBaseNode(n: Node): Node? {
        if (n.getCharset().isEmpty()) {
            return null
        }
        if (!baseNodesMap.containsKey(n.getCharset().first())) {
            return null
        }
        return baseNodesMap[n.getCharset().first()]
    }

    private fun kleenizeNode(n: Node): Node {
        if (n.isKleene() || n.isTop) {
            return n
        }
        return getParentBaseNode(n)!!
    }

    private fun meetInPowerset(n1: Node, n2: Node): Node {
        return powersetLattice.meet(kleenizeNode(n1), kleenizeNode(n2))
    }

    fun respectCharsetUnionLimits(n1: Node, n2: Node): Boolean {
        val parentBaseNode1 = getParentBaseNode(n1)
        val parentBaseNode2 = getParentBaseNode(n2)
        return parentBaseNode1 == parentBaseNode2
    }

    fun respectsIntervalLimits(n: Node): Boolean {
        if (n.getCharset().isEmpty()) {
            return true
        }
        val parentBaseNode = getParentBaseNode(n)
        if (parentBaseNode == null) {
            // Could not find matching parent base node!
            return false
        }
        if (n.getInterval().first < parentBaseNode.getInterval().first ||
            n.getInterval().second > parentBaseNode.getInterval().second
        ) {
            return false
        }
        return true
    }

    fun meet(n1: Node, n2: Node): Node {
        if (n1.isKleene() || n2.isKleene() || !respectCharsetUnionLimits(n1, n2)) {
            return meetInPowerset(n1, n2)
        }
        var minLeft = n1.getInterval().first
        if (n2.getInterval().first < minLeft) {
            minLeft = n2.getInterval().first
        }
        var maxRight = n1.getInterval().second
        if (n2.getInterval().second > maxRight) {
            maxRight = n2.getInterval().second
        }
        val proposedIntervalNode = Node(
            n1.getCharset().union(n2.getCharset()),
            Pair(minLeft, maxRight)
        )
        if (!respectsIntervalLimits(proposedIntervalNode)) {
            return meetInPowerset(n1, n2)
        }
        return proposedIntervalNode
    }
}
