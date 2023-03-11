package zhe.ParSy.Regex

import org.slf4j.LoggerFactory

data class CompressionResult(
    val rule: String,
    val isTop: Boolean
)

class Compressor(
    private val nf: NodeFactory,
    // TODO: remove default of empty list of nodes and accept lattice
    // instead. This is here just while I am breaking everything improving the
    // lattice.
    private val baseNodes: List<Node> = listOf<Node>(),
    private val disjointNodes: List<Pair<Node, Node>> = listOf<Pair<Node, Node>>()
) {
    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    private val lattice = Lattice(baseNodes, disjointNodes)
    private val formatter = Formatter(lattice)

    companion object {
        public fun newBasic(): Compressor {
            return Compressor(NodeFactory())
        }
    }

    fun compressNodes(
        // We assume the previous regexes already are well-behaved. So we only
        // need to format the incoming, new, token nodes.
        prevRegexNodes: List<Node>,
        tokenNodes: List<Node>
    ): List<Node> {
        val fmtPrev = formatter.formatNodes(prevRegexNodes)
        val fmtTokenNodes = formatter.formatNodes(tokenNodes)

        if (fmtPrev.isEmpty()) {
            return fmtTokenNodes
        }
        if (fmtPrev == fmtTokenNodes) {
            return fmtTokenNodes
        }
        if (fmtPrev.size != fmtTokenNodes.size) {
            return listOf(topNode())
        }

        // TODO: refactor this loop
        var tokenIdx: Int = 0
        var prevIdx: Int = 0
        var compressedNodes = listOf<Node>()
        while (tokenIdx < fmtTokenNodes.size) {
            val curNode = fmtPrev[prevIdx]
            val newNode = fmtTokenNodes[tokenIdx]
            val glb = lattice.meet(curNode, newNode)
            if (glb.isTop) {
                return listOf(topNode())
            }
            compressedNodes += glb
            prevIdx++
            tokenIdx++
        }

        return compressedNodes
    }

    fun compress(prevRegex: String, token: String): CompressionResult {
        val prevRegexNodes = formatter.formatNodes(nf.buildNodes(prevRegex))
        val tokenNodes = nf.buildNodes(token)
        val compressedNodes = compressNodes(prevRegexNodes, tokenNodes)
        if (compressedNodes.size == 1 && compressedNodes[0].isTop) {
            return CompressionResult(dotStar, true)
        }
        return CompressionResult(NodeFactory().buildString(compressedNodes), false)
    }

    fun compressToString(prevRegex: String, token: String): String {
        val compressResult = compress(prevRegex, token)
        return compressResult.rule
    }
}
