package zhe.ParSy.Regex

import org.slf4j.LoggerFactory

class PowersetLattice(
    private val baseNodes: List<Node>,
    private val disjointBaseNodes: List<Pair<Node, Node>>
) {
    public val top = topNode()

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
        val glb = n1
        glb.addCharset(n2.getCharset())
        glb.kleenize()
        return glb
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
            logger.debug("do")
            return true
        }
        val interval = n.getInterval()
        val baseNode = getBaseNode(n)
        if (baseNode == null) {
            logger.debug("da")
            return false
        }
        val baseNodeInterval = baseNode.getInterval()
        logger.debug("di")
        return isIntervalWithinBounds(interval, baseNodeInterval)
    }

    fun meet(n1: Node, n2: Node): Node {
        logger.debug("Meeting nodes $n1 and $n2")
        if (n1.isTop || n2.isTop) {
            logger.debug("Result: top")
            return top
        }
        if (n1.getCharset().isEmpty()) {
            return n2
        } else if (n2.getCharset().isEmpty()) {
            return n1
        }
        if (n1.isKleene() || n2.isKleene()) {
            logger.debug("Result: powerset")
            val res = meetInPowerset(n1, n2)
            logger.debug("Res: $res")
            return res
        }
        if (!areNodesCompatible(n1, n2)) {
            logger.debug("Result: powerset")
            val res = elevateAndMeetInPowerset(n1, n2)
            logger.debug("Res: $res")
            return res
        }
        logger.debug("Result: interval node")
        val res = intervalMeet(n1, n2)
        logger.debug("Res: $res")
        return res
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
        val nref = baseNodesMap[n.getCharset().first()]!!
        return Node(nref.getCharset(), nref.getInterval())
    }

    private fun elevateToPowerset(n: Node): Node {
        val baseNode = getBaseNode(n)!!
        baseNode.kleenize()
        return baseNode
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
            logger.debug("Node $intervalNode not within bounds!")
            return elevateAndMeetInPowerset(n1, n2)
        }
        return intervalNode
    }
}
