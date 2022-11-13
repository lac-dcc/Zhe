package zhe.ParSy.Regex

import kotlin.math.min

class Rule(val pattern: String) {}

class Node(val rule: String, val parents: Set<Node>, val level: Int) {
    private val id: Int = globalID

    companion object {
	var globalID: Int = 0
	var allNodes = mutableMapOf<String, Node>()
    }

    init {
	globalID++
	allNodes[rule] = this
    }

    fun isTop(): Boolean {
	return this.id == 0
    }
}

fun allParents(node: Node): Set<Node> {
    if (node.isTop()) {
	return setOf(node)
    }
    var parents = mutableSetOf<Node>(node)
    node.parents.forEach {
	parents += allParents(it)
    }
    return parents
}

class Lattice {
    public var top: Node
    public var bottom: Node

    private var level1Map = mutableMapOf<String, Node>()

    constructor() {
	Node.globalID = 0

	// Top level
	val topLevel = 4
	top = Node(".*", setOf<Node>(), 4)
	val setTop = setOf(top)

	// Level 3
	val alnumLevel: Int = topLevel-1
	val alnumStarNode = Node(alnumStar, setTop, alnumLevel)

	// Level 2
	val starLevel: Int = alnumLevel-1
	val alphaStarNode = Node(alphaStar, setOf(alnumStarNode), starLevel)
	val numStarNode = Node(numStar, setOf(alnumStarNode), starLevel)
	val punctStarNode = Node(punctStar, setTop, starLevel)

	// Level 1
	var charLevel: Int = starLevel-1
	var level1Nodes = mutableSetOf<Node>()
	for (rule in allAlphas) {
	    level1Nodes += Node(rule, setOf(alphaStarNode), charLevel)
	}
	for (rule in allNums) {
	    level1Nodes += Node(rule, setOf(numStarNode), charLevel)
	}
	for (rule in allPuncts) {
	    level1Nodes += Node(rule, setOf(punctStarNode), charLevel)
	}

	// Bottom level
	bottom = Node("", level1Nodes, charLevel-1)

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

    fun transform(prevRegex: String, token: String): String {	
        if (prevRegex == "") {
	    return token
	}
	if (prevRegex == token) {
	    return token
	}

	var prevRegexIdx: Int = 0
	var tokenIdx: Int = 0
	var prevNode = Node.allNodes.getValue(parseTokenPrefix(prevRegex,
							 prevRegexIdx))
	var curNode = prevNode
	var finalRegex = ""
	while (tokenIdx < token.length) {
	    println("New iteration.")
	    println("finalRegex: $finalRegex")
	    println("tokenIdx: $tokenIdx")
	    println("prevRegexIdx: $prevRegexIdx")
	    println("prevNode.rule: ${prevNode.rule}")
	    println("curNode.rule: ${curNode.rule}")

	    val newNode = Node.allNodes.getValue(parseTokenPrefix(token, tokenIdx))
	    if (prevNode.isTop() || newNode.isTop()) {
		return top.rule
	    }

	    val lub: Node = meet(curNode, newNode)

	    println("newNode.rule: ${newNode.rule}")
	    println("lub.rule: ${lub.rule}")

	    if (!lub.isTop()) {
		if (!(lub.level > 1 && finalRegex.endsWith(lub.rule))) {
		    println("Adding '${lub.rule}' to final regex")
		    finalRegex += lub.rule
		    curNode = lub
		}
		tokenIdx += newNode.rule.length
	    }

	    if (lub.isTop()) {
		println("Backtracking. finalRegex before: $finalRegex")

		val finalNode = Node.allNodes.getValue(
		    parseTokenSuffix(finalRegex, finalRegex.length))
		if (meet(finalNode, curNode).isTop()) {
                    prevRegexIdx -= prevNode.rule.length
		} else {
                    tokenIdx -= newNode.rule.length
		    println("Subtracting ${newNode.rule.length} from tokenIdx")
		}
		if (tokenIdx < 0) {
		    tokenIdx = 0
		}

		val finalResult = backtrack(finalRegex, finalRegex.length -
					       parseTokenSuffix(
						   finalRegex,
						   finalRegex.length).length)
		val prevResult = backtrack(prevRegex, prevRegexIdx)
		val tokenResult = backtrack(token, tokenIdx)

		// Meet all of them
		var lubBacktrack = meet(finalResult.node, prevResult.node)
		lubBacktrack = meet(lubBacktrack, tokenResult.node)
		if (!lubBacktrack.isTop()) {
		    if (lubBacktrack.level == 1 && (finalResult.right - finalResult.left) > 0) {
			lubBacktrack = lubBacktrack.parents.toList()[0]
		    }
		    finalRegex = finalRegex.substring(0, finalResult.left) +
		                 lubBacktrack.rule
		}

		prevRegexIdx = prevResult.right
		tokenIdx = tokenResult.right

		println("New prevRegexIdx: $prevRegexIdx")
		println("New tokenIdx: $tokenIdx")

		if (prevRegexIdx >= prevRegex.length) {
		    break
		}
		prevNode = Node.allNodes.getValue(parseTokenPrefix(prevRegex,
							     prevRegexIdx))
		curNode = prevNode

		println("finalRegex after backtracking: $finalRegex")

	    } else if (prevNode.level == 1) {
		prevRegexIdx += prevNode.rule.length
		if (prevRegexIdx >= prevRegex.length) {
		    break
		}
		prevNode = Node.allNodes.getValue(parseTokenPrefix(prevRegex,
							     prevRegexIdx))
		curNode = prevNode
	    }
	}

	println("Final regex before adding leftover: $finalRegex")

	// Add leftovers from previous regex and token, if there are any.
	curNode = Node.allNodes.getValue(parseTokenSuffix(finalRegex,
							  finalRegex.length))
	val leftoverPrev = getLeftover(prevRegex, prevRegexIdx)
	if (leftoverPrev.isTop()) {
	    return top.rule
	}
	val lubPrev = meet(curNode, leftoverPrev)
	if (lubPrev.rule != curNode.rule) {
	    finalRegex += leftoverPrev.rule
	}
	println("final first left over: ${curNode.rule}")
	val leftoverToken = getLeftover(token, tokenIdx)
	if (leftoverToken.isTop()) {
	    return top.rule
	}
	val lubToken = meet(lubPrev, leftoverToken)
	if (lubPrev.rule != lubToken.rule) {
	    finalRegex += leftoverToken.rule
	}

	println("Final regex after adding leftovers: $finalRegex")

	if (finalRegex == "") {
	    return top.rule
	}

	return finalRegex
    }

    data class BacktrackResult(val regex: String, 
			       val left: Int,
			       val right: Int,
			       val node: Node)

    fun backtrack(s: String, prevOffset: Int): BacktrackResult {
	println("Backtracking $s with offset $prevOffset")

	var parsedToken = parseTokenPrefix(s, prevOffset)
	println("Parsed token: $parsedToken")
	var curNode = Node.allNodes.getValue(parsedToken)
	var offset: Int = prevOffset

	// Propose to parse the suffix from the right, instead of from the left.
	var proposal: Node? = null
	if (curNode.level < 2 && prevOffset+1 < s.length) {
	    proposal = Node.allNodes.getValue(
		parseTokenSuffix(s, prevOffset+1))
	    if (proposal.level > curNode.level) {
		curNode = proposal
		offset = prevOffset+1
	    } else {
		// Reject proposal
		proposal = null
	    }
	}

	var left: Int
	var right: Int

	println("Cur node before doing anything: ${curNode.rule}")

	// Go backwards and forwards eating up everything we can.
	//
	// Backward
	while (offset > 0) {
	    var newNode = Node.allNodes.getValue(parseTokenSuffix(s, offset))

	    val lub = meet(curNode, newNode)
	    if (lub.isTop()) {
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
	    var newNode = Node.allNodes.getValue(parseTokenPrefix(s, offset))

	    val lub = meet(curNode, newNode)
	    if (lub.isTop()) {
		break
	    }

	    offset += newNode.rule.length
	    curNode = lub
	}
	right = offset

	if (proposal != null) {
	    right++
	}

	println("Result of backtracking $s: ${curNode.rule}")

	return BacktrackResult(curNode.rule, left, right, curNode)
    }

    fun getLeftover(s: String, sidx: Int): Node {
	println("Getting leftover for $s sidx: $sidx")

	if (sidx >= s.length) {
	    // No leftover
	    println("No leftover")
	    return bottom
	}

	var idx = sidx
	var curNode = Node.allNodes.getValue(parseTokenPrefix(s, idx))

	idx += curNode.rule.length

	if (curNode.level == 1) {
	    // Assume only one parent exists.
	    curNode = curNode.parents.toList()[0]
	}

	while (idx < s.length) {
	    val newRegex = parseTokenPrefix(s, idx)
	    val newNode = Node.allNodes.getValue(newRegex)
	    val lub: Node = meet(curNode, newNode)

	    println("curNode.rule: ${curNode.rule}")
	    println("newNode.rule: ${newNode.rule}")
	    println("lub.rule: ${lub.rule}")

	    if (lub.isTop()) {
		// No match!
		return top
	    }
	    curNode = lub
	    idx += newNode.rule.length
	}

	println("Leftover: ${curNode.rule}")
	return curNode
    }
}
