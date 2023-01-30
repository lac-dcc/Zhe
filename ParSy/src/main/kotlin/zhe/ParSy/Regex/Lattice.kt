package zhe.ParSy.Regex

class PowersetLattice(private val baseNodes: List<Node>) {
    fun meet(n1: Node, n2: Node): Node {
        return Node(
            n1.charset.union(n2.charset),
            kleeneInterval
        )
    }
}

class Lattice(private val baseNodes: List<Node>) {
    private val top = dummyNode().apply { this.isTop = true }
    private val powersetLattice = PowersetLattice(baseNodes)
    private val baseNodesMap = mutableMapOf<Char, Node>()
    init {
        // Assume the charsets of the base nodes are disjoint
        //
        // TODO: is there a way to avoid this assumption?
        baseNodes.forEach { node ->
            node.charset.forEach { c ->
                baseNodesMap[c] = node
            }
        }
    }

    private fun getParentBaseNode(n: Node): Node? {
        if (n.charset.isEmpty()) {
            return null
        }
        if (!baseNodesMap.containsKey(n.charset.first())) {
            return null
        }
        return baseNodesMap[n.charset.first()]
    }

    private fun kleenizeNode(n: Node): Node {
        if (n.isKleene()) {
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
        if (n.charset.isEmpty()) {
            return true
        }
        val parentBaseNode = getParentBaseNode(n)
        if (parentBaseNode == null) {
            // Could not find matching parent base node!
            return false
        }
        if (n.interval.first < parentBaseNode.interval.first ||
            n.interval.second > parentBaseNode.interval.second
        ) {
            return false
        }
        return true
    }

    fun meet(n1: Node, n2: Node): Node {
        if (n1.isKleene() || n2.isKleene() || !respectCharsetUnionLimits(n1, n2)) {
            return meetInPowerset(n1, n2)
        }
        var minLeft = n1.interval.first
        if (n2.interval.first < minLeft) {
            minLeft = n2.interval.first
        }
        var maxRight = n1.interval.second
        if (n2.interval.second > maxRight) {
            maxRight = n2.interval.second
        }
        val proposedIntervalNode = Node(
            n1.charset.union(n2.charset),
            Pair(minLeft, maxRight)
        )
        if (!respectsIntervalLimits(proposedIntervalNode)) {
            return meetInPowerset(n1, n2)
        }
        return proposedIntervalNode
    }
}
