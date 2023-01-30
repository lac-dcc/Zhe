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

    fun compressNodes(
        prevRegexNodes: List<Node>,
        tokenNodes: List<Node>
    ): CompressionResult {
        if (prevRegexNodes.isEmpty()) {
            return CompressionResult(nf.buildString(tokenNodes), false)
        }
        if (prevRegexNodes == tokenNodes) {
            return CompressionResult(nf.buildString(tokenNodes), false)
        }

        var tokenIdx: Int = 0
        var prevIdx: Int = 0
        var compressedNodes = listOf<Node>()
        while (tokenIdx < tokenNodes.size) {
            logger.debug("New iteration.")
            // logger.debug("finalRegex: $finalRegex")
            // logger.debug("tokenIdx: $tokenIdx")
            // logger.debug("prevRegexIdx: $prevRegexIdx")
            // logger.debug("prevNode.rule: ${prevNode.rule}")
            // logger.debug("curNode.rule: ${curNode.rule}")

            val curNode = prevRegexNodes[prevIdx]
            val newNode = tokenNodes[tokenIdx]
            // if (lattice.isTop(prevNode) || lattice.isTop(newNode)) {
            //     return CompressionResult(lattice.top.rule, true)
            // }

            val lub = lattice.meet(curNode, newNode)
            compressedNodes += lub
            prevIdx++
            tokenIdx++
            // logger.debug("newNode.rule: ${newNode.rule}")
            // logger.debug("lub.rule: ${lub.rule}")

            // if (lattice.isTop(lub)) {
            //     logger.debug("Backtracking. finalRegex before: $finalRegex")

            //     val finalNode = nf.getBySuffix(finalRegex, finalRegex.length)
            //     if (lattice.isTop(lattice.meet(finalNode, curNode))) {
            //         prevRegexIdx -= prevNode.rule.length
            //     } else {
            //         tokenIdx -= newNode.rule.length
            //         logger.debug("Subtracting ${newNode.rule.length} from tokenIdx")
            //     }
            //     if (tokenIdx < 0) {
            //         tokenIdx = 0
            //     }

            //     val finalResult = backtrack(
            //         finalRegex,
            //         finalRegex.length -
            //             parseTokenSuffix(
            //                 finalRegex,
            //                 finalRegex.length
            //             ).length
            //     )
            //     val prevResult = backtrack(prevRegex, prevRegexIdx)
            //     val tokenResult = backtrack(token, tokenIdx)

            //     // Meet all of them
            //     var lubBacktrack = lattice.meet(finalResult.node, prevResult.node)
            //     lubBacktrack = lattice.meet(lubBacktrack, tokenResult.node)
            //     if (!lattice.isTop(lubBacktrack)) {
            //         if (lubBacktrack.level == 1 && (finalResult.right - finalResult.left) > 0) {
            //             lubBacktrack = lubBacktrack.parents.toList()[0]
            //         }
            //         finalRegex = finalRegex.substring(0, finalResult.left) +
            //             lubBacktrack.rule
            //     }

            //     prevRegexIdx = prevResult.right
            //     tokenIdx = tokenResult.right

            //     logger.debug("New prevRegexIdx: $prevRegexIdx")
            //     logger.debug("New tokenIdx: $tokenIdx")

            //     if (prevRegexIdx >= prevRegex.length) {
            //         break
            //     }
            //     prevNode = nf.getByPrefix(prevRegex, prevRegexIdx)
            //     curNode = prevNode

            //     logger.debug("finalRegex after backtracking: $finalRegex")
            // } else if (prevNode.level == 1) {
            //     prevRegexIdx += prevNode.rule.length
            //     if (prevRegexIdx >= prevRegex.length) {
            //         break
            //     }
            //     prevNode = nf.getByPrefix(prevRegex, prevRegexIdx)
            //     curNode = prevNode
            // }
        }

        logger.debug("State before adding leftovers: tokenIdx=${tokenIdx} " +
                     "prevIdx=${prevIdx}")

        // Add leftovers from previous regex and token, if there are any.
        val curNode = compressedNodes[compressedNodes.size-1]
        val leftoverPrev = getLeftover(prevRegexNodes, prevIdx)
        if (leftoverPrev.isTop) {
            // TODO
            return CompressionResult("", true)
        }
        val lubPrev = lattice.meet(curNode, leftoverPrev)
        if (lubPrev.charset != curNode.charset) {
            compressedNodes += leftoverPrev
        }
        logger.debug("final first left over: ${curNode}")
        val leftoverToken = getLeftover(tokenNodes, tokenIdx)
        if (leftoverToken.isTop) {
            // TODO
            return CompressionResult("", true)
        }
        val lubToken = lattice.meet(lubPrev, leftoverToken)
        if (lubPrev.charset != lubToken.charset) {
            compressedNodes += leftoverToken
        }

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

    // data class BacktrackResult(
    //     val regex: String,
    //     val left: Int,
    //     val right: Int,
    //     val node: Node
    // )

    // fun backtrack(s: String, prevOffset: Int): BacktrackResult {
    //     logger.debug("Backtracking $s with offset $prevOffset")

    //     var curNode = nf.getByPrefix(s, prevOffset)
    //     var offset: Int = prevOffset

    //     // Propose to parse the suffix from the right, instead of from the left.
    //     var proposal: Node? = null
    //     if (curNode.level < 2 && prevOffset + 1 < s.length) {
    //         proposal = nf.getBySuffix(s, prevOffset + 1)
    //         if (proposal.level > curNode.level) {
    //             curNode = proposal
    //             offset = prevOffset + 1
    //         } else {
    //             // Reject proposal
    //             proposal = null
    //         }
    //     }

    //     var left: Int
    //     var right: Int

    //     logger.debug("Cur node before doing anything: ${curNode.rule}")

    //     // Go backwards and forwards eating up everything we can.
    //     //
    //     // Backward
    //     while (offset > 0) {
    //         var newNode = nf.getBySuffix(s, offset)

    //         val lub = lattice.meet(curNode, newNode)
    //         if (lattice.isTop(lub)) {
    //             break
    //         }

    //         offset -= newNode.rule.length
    //         curNode = lub
    //     }
    //     left = offset
    //     //
    //     // Forward
    //     offset = prevOffset
    //     while (offset < s.length) {
    //         var newNode = nf.getByPrefix(s, offset)

    //         val lub = lattice.meet(curNode, newNode)
    //         if (lattice.isTop(lub)) {
    //             break
    //         }

    //         offset += newNode.rule.length
    //         curNode = lub
    //     }
    //     right = offset

    //     if (proposal != null) {
    //         right++
    //     }

    //     logger.debug("Result of backtracking $s: ${curNode.rule}")

    //     return BacktrackResult(curNode.rule, left, right, curNode)
    // }

    fun getLeftover(nodes: List<Node>, startIdx: Int): Node {
        logger.debug("Getting leftover with startIdx: $startIdx")

        if (startIdx >= nodes.size) {
            // No leftover
            logger.debug("No leftover")
            return dummyNode()
        }

        var idx = startIdx
        var curNode = nodes[startIdx]
        idx++

        // if (curNode.level == 1) {
        //     // Assume only one parent exists.
        //     curNode = curNode.parents.toList()[0]
        // }

        while (idx < nodes.size) {
            val newNode = nodes[idx]
            val lub = lattice.meet(curNode, newNode)

            logger.debug("curNode: ${curNode}")
            logger.debug("newNode: ${newNode}")
            logger.debug("lub: ${lub}")

            if (lub.isTop) {
                // No match!
                // TODO
                return dummyNode()
            }
            curNode = lub
            idx++
        }

        logger.debug("Leftover: ${curNode}")
        return curNode
    }
}
