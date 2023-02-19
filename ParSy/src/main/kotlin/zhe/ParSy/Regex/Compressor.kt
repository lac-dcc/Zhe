package zhe.ParSy.Regex

import org.slf4j.LoggerFactory

data class CompressionResult(
    val rule: String,
    val isTop: Boolean
)

class Compressor(
    private val nf: NodeFactory,
    // TODO: remove default of empty list of nodes
    private val baseNodes: List<Node> = listOf<Node>()
) {
    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    private val lattice = Lattice(baseNodes)

    companion object {
        public fun newBasic(): Compressor {
            return Compressor(NodeFactory())
        }
    }

    fun compress(prevRegex: String, token: String): CompressionResult {
        val prevRegexNodes = nf.buildNodes(prevRegex)
        val tokenNodes = nf.buildNodes(token)
        return compressNodes(prevRegexNodes, tokenNodes)
    }

    // TODO: move this somewhere else
    fun formatNodes(tokens: List<Node>): List<Node> {
        if (tokens.isEmpty()) {
            return tokens
        }
        
        return tokens
    }

    fun compressNodes(
        // We assume the previous regexes already are well-behaved. So we only
        // need to format the incoming, new, token nodes.
        prevRegexNodes: List<Node>,
        tokenNodes: List<Node>
    ): CompressionResult {
        if (prevRegexNodes.isEmpty()) {
            return CompressionResult(nf.buildString(tokenNodes), false)
        }
        if (prevRegexNodes == tokenNodes) {
            return CompressionResult(nf.buildString(tokenNodes), false)
        }

        val fmtTokenNodes = formatNodes(tokenNodes)

        var tokenIdx: Int = 0
        var prevIdx: Int = 0
        var compressedNodes = listOf<Node>()
        while (tokenIdx < fmtTokenNodes.size) {
            logger.debug("New iteration.")
            // logger.debug("finalRegex: $finalRegex")
            // logger.debug("tokenIdx: $tokenIdx")
            // logger.debug("prevRegexIdx: $prevRegexIdx")
            // logger.debug("prevNode.rule: ${prevNode.rule}")
            // logger.debug("curNode.rule: ${curNode.rule}")

            val curNode = prevRegexNodes[prevIdx]
            val newNode = fmtTokenNodes[tokenIdx]
            // if (lattice.isTop(prevNode) || lattice.isTop(newNode)) {
            //     return CompressionResult(lattice.top.rule, true)
            // }

            val lub = lattice.meet(curNode, newNode)
            compressedNodes += lub
            prevIdx++
            tokenIdx++
            // logger.debug("newNode.rule: ${newNode.rule}")
            // logger.debug("lub.rule: ${lub.rule}")
        }

        logger.debug("State before adding leftovers: tokenIdx=${tokenIdx} " +
                     "prevIdx=${prevIdx}")

        // logger.debug("Final regex after adding leftovers: $finalRegex")

        // if (finalRegex == "") {
        //     return CompressionResult(lattice.top.rule, true)
        // }

        return CompressionResult(nf.buildString(compressedNodes), false)
    }

    fun compressToString(prevRegex: String, token: String): String {
        val compressResult = compress(prevRegex, token)
        return compressResult.rule
    }
}
