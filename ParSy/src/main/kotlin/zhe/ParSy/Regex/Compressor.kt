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

    companion object {
        public fun newBasic(): Compressor {
            return Compressor(NodeFactory())
        }
    }

    // TODO: move this somewhere else
    fun formatNodes(nodes: List<Node>): List<Node> {
        logger.debug("Formatting nodes: $nodes")

        if (nodes.isEmpty()) {
            return listOf<Node>()
        } else if (nodes.size == 1) {
            return nodes
        }

        val formattedNodes = mutableListOf<Node>(nodes[0])
        var fmtIdx = 0
        var origIdx = 1
        while (origIdx < nodes.size) {
            // Expand upper bound to accomodate one more character.
            val origNode = nodes[origIdx]
            val fmtNode = formattedNodes[fmtIdx]
            origIdx++

            val glb = lattice.meet(fmtNode, origNode)
            if (glb.isTop) {
                formattedNodes += origNode
                fmtIdx++
                continue
            }

            // We want a node [a]{1,1}[b]{1,1}[c]{1,1} to become [abc]{3,3}
            formattedNodes[fmtIdx] = glb.apply {
                if (!isKleene() && !lattice.isBaseNode(glb)) {
                    capInterval()
                    incrementInterval()
                }
            }
        }

        logger.debug("Formatted nodes: $formattedNodes")

        return formattedNodes
    }

    fun compress(prevRegex: String, token: String): CompressionResult {
        val prevRegexNodes = formatNodes(nf.buildNodes(prevRegex))
        logger.debug("Previous regexes in 'compress': $prevRegexNodes")
        val tokenNodes = nf.buildNodes(token)
        return compressNodes(prevRegexNodes, tokenNodes)
    }

    fun compressNodes(
        // We assume the previous regexes already are well-behaved. So we only
        // need to format the incoming, new, token nodes.
        prevRegexNodes: List<Node>,
        tokenNodes: List<Node>
    ): CompressionResult {
        val fmtTokenNodes = formatNodes(tokenNodes)

        if (prevRegexNodes.isEmpty()) {
            return CompressionResult(nf.buildString(fmtTokenNodes), false)
        }
        if (prevRegexNodes == fmtTokenNodes) {
            return CompressionResult(nf.buildString(fmtTokenNodes), false)
        }
        if (prevRegexNodes.size != fmtTokenNodes.size) {
            return CompressionResult(nf.buildString(listOf(topNode())), true)
        }

        var tokenIdx: Int = 0
        var prevIdx: Int = 0
        var compressedNodes = listOf<Node>()
        while (tokenIdx < fmtTokenNodes.size) {
            val curNode = prevRegexNodes[prevIdx]
            val newNode = fmtTokenNodes[tokenIdx]
            val glb = lattice.meet(curNode, newNode)
            if (glb.isTop) {
                return CompressionResult(nf.buildString(listOf(topNode())), true)
            }
            compressedNodes += glb
            prevIdx++
            tokenIdx++
        }

        return CompressionResult(nf.buildString(compressedNodes), false)
    }

    fun compressToString(prevRegex: String, token: String): String {
        val compressResult = compress(prevRegex, token)
        return compressResult.rule
    }
}
