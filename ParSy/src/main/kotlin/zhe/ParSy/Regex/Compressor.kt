package zhe.ParSy.Regex

import org.slf4j.LoggerFactory

data class CompressResult(
    val rule: String,
    val isTop: Boolean
)

class Compressor(
    private val nf: NodeFactory
) {
    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    private val lattice = Lattice(nf)

    companion object {
        public fun newBasic(): Compressor {
            return Compressor(NodeFactory())
        }
    }

    fun compress(prevRegex: String, token: String): CompressResult {
        if (prevRegex == "") {
            return CompressResult(token, lattice.isTop(token))
        }
        if (prevRegex == token) {
            return CompressResult(token, lattice.isTop(token))
        }

        var prevRegexIdx: Int = 0
        var tokenIdx: Int = 0
        var prevNode = nf.getByPrefix(prevRegex, prevRegexIdx)
        var curNode = prevNode
        var finalRegex = ""
        while (tokenIdx < token.length) {
            logger.debug("New iteration.")
            logger.debug("finalRegex: $finalRegex")
            logger.debug("tokenIdx: $tokenIdx")
            logger.debug("prevRegexIdx: $prevRegexIdx")
            logger.debug("prevNode.rule: ${prevNode.rule}")
            logger.debug("curNode.rule: ${curNode.rule}")

            val newNode = nf.getByPrefix(token, tokenIdx)
            if (lattice.isTop(prevNode) || lattice.isTop(newNode)) {
                return CompressResult(lattice.top.rule, true)
            }

            val lub = lattice.meet(curNode, newNode)

            logger.debug("newNode.rule: ${newNode.rule}")
            logger.debug("lub.rule: ${lub.rule}")

            if (!lattice.isTop(lub)) {
                if (!(lub.level > 1 && finalRegex.endsWith(lub.rule))) {
                    logger.debug("Adding '${lub.rule}' to final regex")
                    finalRegex += lub.rule
                    curNode = lub
                }
                tokenIdx += newNode.rule.length
            }

            if (lattice.isTop(lub)) {
                logger.debug("Backtracking. finalRegex before: $finalRegex")

                val finalNode = nf.getBySuffix(finalRegex, finalRegex.length)
                if (lattice.isTop(lattice.meet(finalNode, curNode))) {
                    prevRegexIdx -= prevNode.rule.length
                } else {
                    tokenIdx -= newNode.rule.length
                    logger.debug("Subtracting ${newNode.rule.length} from tokenIdx")
                }
                if (tokenIdx < 0) {
                    tokenIdx = 0
                }

                val finalResult = backtrack(
                    finalRegex,
                    finalRegex.length -
                        parseTokenSuffix(
                            finalRegex,
                            finalRegex.length
                        ).length
                )
                val prevResult = backtrack(prevRegex, prevRegexIdx)
                val tokenResult = backtrack(token, tokenIdx)

                // Meet all of them
                var lubBacktrack = lattice.meet(finalResult.node, prevResult.node)
                lubBacktrack = lattice.meet(lubBacktrack, tokenResult.node)
                if (!lattice.isTop(lubBacktrack)) {
                    if (lubBacktrack.level == 1 && (finalResult.right - finalResult.left) > 0) {
                        lubBacktrack = lubBacktrack.parents.toList()[0]
                    }
                    finalRegex = finalRegex.substring(0, finalResult.left) +
                        lubBacktrack.rule
                }

                prevRegexIdx = prevResult.right
                tokenIdx = tokenResult.right

                logger.debug("New prevRegexIdx: $prevRegexIdx")
                logger.debug("New tokenIdx: $tokenIdx")

                if (prevRegexIdx >= prevRegex.length) {
                    break
                }
                prevNode = nf.getByPrefix(prevRegex, prevRegexIdx)
                curNode = prevNode

                logger.debug("finalRegex after backtracking: $finalRegex")
            } else if (prevNode.level == 1) {
                prevRegexIdx += prevNode.rule.length
                if (prevRegexIdx >= prevRegex.length) {
                    break
                }
                prevNode = nf.getByPrefix(prevRegex, prevRegexIdx)
                curNode = prevNode
            }
        }

        logger.debug("Final regex before adding leftover: $finalRegex")

        // Add leftovers from previous regex and token, if there are any.
        curNode = nf.getBySuffix(finalRegex, finalRegex.length)
        val leftoverPrev = getLeftover(prevRegex, prevRegexIdx)
        if (lattice.isTop(leftoverPrev)) {
            return CompressResult(lattice.top.rule, true)
        }
        val lubPrev = lattice.meet(curNode, leftoverPrev)
        if (lubPrev.rule != curNode.rule) {
            finalRegex += leftoverPrev.rule
        }
        logger.debug("final first left over: ${curNode.rule}")
        val leftoverToken = getLeftover(token, tokenIdx)
        if (lattice.isTop(leftoverToken)) {
            return CompressResult(lattice.top.rule, true)
        }
        val lubToken = lattice.meet(lubPrev, leftoverToken)
        if (lubPrev.rule != lubToken.rule) {
            finalRegex += leftoverToken.rule
        }

        logger.debug("Final regex after adding leftovers: $finalRegex")

        if (finalRegex == "") {
            return CompressResult(lattice.top.rule, true)
        }

        return CompressResult(finalRegex, lattice.isTop(finalRegex))
    }

    fun compressToString(prevRegex: String, token: String): String {
        val compressResult = compress(prevRegex, token)
        return compressResult.rule
    }

    data class BacktrackResult(
        val regex: String,
        val left: Int,
        val right: Int,
        val node: Node
    )

    fun backtrack(s: String, prevOffset: Int): BacktrackResult {
        logger.debug("Backtracking $s with offset $prevOffset")

        var curNode = nf.getByPrefix(s, prevOffset)
        var offset: Int = prevOffset

        // Propose to parse the suffix from the right, instead of from the left.
        var proposal: Node? = null
        if (curNode.level < 2 && prevOffset + 1 < s.length) {
            proposal = nf.getBySuffix(s, prevOffset + 1)
            if (proposal.level > curNode.level) {
                curNode = proposal
                offset = prevOffset + 1
            } else {
                // Reject proposal
                proposal = null
            }
        }

        var left: Int
        var right: Int

        logger.debug("Cur node before doing anything: ${curNode.rule}")

        // Go backwards and forwards eating up everything we can.
        //
        // Backward
        while (offset > 0) {
            var newNode = nf.getBySuffix(s, offset)

            val lub = lattice.meet(curNode, newNode)
            if (lattice.isTop(lub)) {
                break
            }

            offset -= newNode.rule.length
            curNode = lub
        }
        left = offset
        //
        // Forward
        offset = prevOffset
        while (offset < s.length) {
            var newNode = nf.getByPrefix(s, offset)

            val lub = lattice.meet(curNode, newNode)
            if (lattice.isTop(lub)) {
                break
            }

            offset += newNode.rule.length
            curNode = lub
        }
        right = offset

        if (proposal != null) {
            right++
        }

        logger.debug("Result of backtracking $s: ${curNode.rule}")

        return BacktrackResult(curNode.rule, left, right, curNode)
    }

    fun getLeftover(s: String, sidx: Int): Node {
        logger.debug("Getting leftover for $s sidx: $sidx")

        if (sidx >= s.length) {
            // No leftover
            logger.debug("No leftover")
            return lattice.bottom
        }

        var idx = sidx
        var curNode = nf.getByPrefix(s, idx)

        idx += curNode.rule.length

        if (curNode.level == 1) {
            // Assume only one parent exists.
            curNode = curNode.parents.toList()[0]
        }

        while (idx < s.length) {
            val newNode = nf.getByPrefix(s, idx)
            val lub = lattice.meet(curNode, newNode)

            logger.debug("curNode.rule: ${curNode.rule}")
            logger.debug("newNode.rule: ${newNode.rule}")
            logger.debug("lub.rule: ${lub.rule}")

            if (lattice.isTop(lub)) {
                // No match!
                return lattice.top
            }
            curNode = lub
            idx += newNode.rule.length
        }

        logger.debug("Leftover: ${curNode.rule}")
        return curNode
    }
}
