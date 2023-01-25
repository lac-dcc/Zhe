package zhe.ParSy.Regex

// interface BaseLattice {
    
// }

class Lattice(
    private val nf: NodeFactory
) {
    public var top: Node
    public var bottom: Node

    private var level1Map = mutableMapOf<String, Node>()

    init {
        // Top level
        val topLevel = 4
        top = nf.build(dotStar, setOf<Node>(), 4)
        val setTop = setOf(top)

        // Level 3
        val alnumLevel: Int = topLevel - 1
        val alnumStarNode = nf.build(alnumStar, setTop, alnumLevel)

        // Level 2
        val starLevel: Int = alnumLevel - 1
        val alphaStarNode = nf.build(alphaStar, setOf(alnumStarNode), starLevel)
        val numStarNode = nf.build(numStar, setOf(alnumStarNode), starLevel)
        val punctStarNode = nf.build(punctStar, setTop, starLevel)

        // Level 1
        var charLevel: Int = starLevel - 1
        var level1Nodes = mutableSetOf<Node>()
        for (rule in allAlphas) {
            level1Nodes += nf.build(rule, setOf(alphaStarNode), charLevel)
        }
        for (rule in allNums) {
            level1Nodes += nf.build(rule, setOf(numStarNode), charLevel)
        }
        for (rule in allPuncts) {
            level1Nodes += nf.build(rule, setOf(punctStarNode), charLevel)
        }

        // Bottom level
        bottom = nf.build("", level1Nodes, charLevel - 1)

        for (node in level1Nodes) {
            level1Map[node.rule] = node
        }
    }

    fun meet(n1: Node, n2: Node): Node {
        val alln1 = allParents(n1)
        val alln2 = allParents(n2)
        val commonParents = alln1.intersect(alln2)
        var lub: Node = top
        commonParents.forEach {
            if (it.level < lub.level) {
                lub = it
            }
        }
        return lub
    }

    fun isTop(s: String): Boolean {
        return s == top.rule
    }

    fun isTop(node: Node): Boolean {
        return isTop(node.rule)
    }

    // allParents returns all the parents a node has in the lattice. This is
    // used to compute the meet.
    private fun allParents(node: Node): Set<Node> {
        if (isTop(node)) {
            return setOf(node)
        }
        var parents = mutableSetOf<Node>(node)
        node.parents.forEach {
            parents += allParents(it)
        }
        return parents
    }
}
