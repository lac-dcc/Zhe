package zhe.ParSy.Regex

import org.slf4j.LoggerFactory

class PowersetLattice(
    private val baseNodes: List<Node>,
    private val disjointBaseNodes: List<Pair<Node, Node>>
) {
    public val top = dummyNode().apply { this.isTop = true }

    // TODO: improve this function
    fun areDisjoint(n1: Node, n2: Node): Boolean {
        val charSample1: Char = n1.getCharset().take(1)[0]
        val charSample2: Char = n2.getCharset().take(1)[0]
        disjointBaseNodes.forEach {
            val cs1: Set<Char> = it.first.getCharset()
            val cs2: Set<Char> = it.second.getCharset()
            if ((cs1.contains(charSample1) && cs2.contains(charSample2)) ||
                (cs2.contains(charSample1) && cs1.contains(charSample2))
            ) {
                return true
            }
        }
        return false
    }

    fun meet(n1: Node, n2: Node): Node {
        if (areDisjoint(n1, n2)) {
            return top
        }
        return n1.apply { addCharset(n2.getCharset()) }
    }
}

class Lattice(
    private val baseNodes: List<Node>,
    // TODO: improve disjoint base nodes treatment
    private val disjointBaseNodes: List<Pair<Node, Node>>
) {
    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    private val powersetLattice = PowersetLattice(baseNodes, disjointBaseNodes)

    // TODO: Remove top attribute from this class
    private val top = powersetLattice.top

    private val baseNodesMap = mutableMapOf<Char, Node>()
    init {
        // Assume the charsets of the base nodes are disjoint
        //
        // TODO: is there a way to avoid this assumption?
        baseNodes.forEach { node ->
            logger.debug("Adding node $node to base nodes map")
            node.getCharset().forEach { c ->
                baseNodesMap[c] = node
            }
        }
    }

    fun areNodesCompatible(n1: Node, n2: Node): Boolean {
        val parentBaseNode1 = getBaseNode(n1)
        val parentBaseNode2 = getBaseNode(n2)
        return parentBaseNode1 == parentBaseNode2
    }

    // TODO: Make the interval a class and make this its method
    fun isIntervalWithinBounds(
        interval: Pair<UInt, UInt>,
        bounds: Pair<UInt, UInt>
    ): Boolean {
        if (interval.first < bounds.first ||
            interval.second > bounds.second
        ) {
            return false
        }
        return true
    }

    // isNodeWithinBounds returns true if the node's interval is contained in
    // its respective base node interval.
    fun isNodeWithinBounds(n: Node): Boolean {
        if (n.getCharset().isEmpty()) {
            return true
        }
        val interval = n.getInterval()
        val baseNode = getBaseNode(n)
        if (baseNode == null) {
            return false
        }
        val baseNodeInterval = baseNode.getInterval()
        return isIntervalWithinBounds(interval, baseNodeInterval)
    }

    fun meet(n1: Node, n2: Node): Node {
        logger.debug("Meeting nodes $n1 and $n2")
        if (n1.isTop || n2.isTop) {
            return top
        }
        if (n1.isKleene() || n2.isKleene()) {
            return meetInPowerset(n1, n2)
        }
        if (!areNodesCompatible(n1, n2)) {
            return elevateAndMeetInPowerset(n1, n2)
        }
        return intervalMeet(n1, n2)
    }

    fun isBaseNode(n: Node): Boolean {
        // TODO: Make this function more efficient
        return baseNodes.contains(n)
    }

    private fun getBaseNode(n: Node): Node? {
        if (n.getCharset().isEmpty()) {
            return null
        }
        if (!baseNodesMap.containsKey(n.getCharset().first())) {
            return null
        }
        return baseNodesMap[n.getCharset().first()]
    }

    private fun elevateToPowerset(n: Node): Node {
        return getBaseNode(n)!!.apply { kleenize() }
    }

    private fun meetInPowerset(n1: Node, n2: Node): Node {
        return powersetLattice.meet(n1, n2)
    }

    private fun elevateAndMeetInPowerset(n1: Node, n2: Node): Node {
        val pwsetNode1 = elevateToPowerset(n1)
        val pwsetNode2 = elevateToPowerset(n2)
        return meetInPowerset(pwsetNode1, pwsetNode2)
    }

    private fun intervalMeet(n1: Node, n2: Node): Node {
        val intervalNode = Node(n1.joinCharset(n2), n1.joinInterval(n2))
        if (!isNodeWithinBounds(intervalNode)) {
            return elevateAndMeetInPowerset(n1, n2)
        }
        return intervalNode
    }
}
