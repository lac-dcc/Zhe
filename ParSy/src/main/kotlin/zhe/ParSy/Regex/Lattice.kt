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
        logger.debug("Node: $n")
        logger.debug("Parent base node: $parentBaseNode")
        if (parentBaseNode == null) {
            // Could not find matching parent base node!
            return false
        }
        if (n.getInterval().first < parentBaseNode.getInterval().first ||
            n.getInterval().second > parentBaseNode.getInterval().second
        ) {
            logger.debug("Could not find dodo2")
            return false
        }
        return true
    }

    fun meet(n1: Node, n2: Node): Node {
        logger.debug("Meeting nodes $n1 and $n2")
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
        logger.debug("Proposed interval node before: $proposedIntervalNode")
        if (!respectsIntervalLimits(proposedIntervalNode)) {
            val pwsetRes = meetInPowerset(n1, n2)
            logger.debug("Pwsetres = $pwsetRes")
            return pwsetRes
        }
        logger.debug("Proposed interval node: $proposedIntervalNode")
        return proposedIntervalNode
    }

    fun isBaseNode(n: Node): Boolean {
        // TODO: Make this function more efficient
        return baseNodes.contains(n)
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
}
