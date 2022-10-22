package zhe.ParSy.Regex

import kotlin.math.min

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
	parents.plusAssign(allParents(it))
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
	    level1Nodes.plusAssign(Node(rule, setOf(alphaStarNode), charLevel))
	}
	for (rule in allNums) {
	    level1Nodes.plusAssign(Node(rule, setOf(numStarNode), charLevel))
	}
	for (rule in allPuncts) {
	    level1Nodes.plusAssign(Node(rule, setOf(punctStarNode), charLevel))
	}

	// Bottom level
	bottom = Node("", level1Nodes, charLevel-1)

	for (node in level1Nodes) {
	    level1Map[node.rule] = node
	}
    }

    // TODO: improve efficiency of meet! The current method is very very slow
    // -aholmquist 2022-10-22
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
	    val c: String = token[tokenIdx].toString()
	    val newNode = Node.allNodes.getValue(c)
	    val lub: Node = meet(curNode, newNode)

	    if (!lub.isTop()) {
		if (!(lub.level > 1 && finalRegex.endsWith(lub.rule))) {
		    finalRegex += lub.rule
		    curNode = lub
		}
		tokenIdx++
	    }

	    if (lub.isTop()) {
		val finalNode = Node.allNodes.getValue(
		    parseTokenSuffix(finalRegex, finalRegex.length))
		if (meet(finalNode, curNode).isTop()) {
		    prevRegexIdx -= prevNode.rule.length
		} else {
		    tokenIdx--
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

		if (prevRegexIdx >= prevRegex.length) {
		    break
		}
		prevNode = Node.allNodes.getValue(parseTokenPrefix(prevRegex,
							     prevRegexIdx))
		curNode = prevNode

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

	var leftover: String
	leftover = addLeftover(prevRegex, prevRegexIdx)
	if (leftover == top.rule) {
	    return top.rule
	}
	if (!finalRegex.endsWith(leftover)) {
	    finalRegex += leftover
	}
	leftover = addLeftover(token, tokenIdx)
	if (leftover == top.rule) {
	    return top.rule
	}
	if (!finalRegex.endsWith(leftover)) {
	    finalRegex += leftover
	}

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
	var curNode = Node.allNodes.getValue(parseTokenPrefix(s, prevOffset))
	var offset: Int = prevOffset

	val left: Int
	val right: Int

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

	return BacktrackResult(curNode.rule, left, right, curNode)
    }

    fun addLeftover(s: String, sidx: Int): String {
	if (sidx >= s.length) {
	    // No leftover
	    return ""
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

	    if (lub.isTop()) {
		// No match!
		return top.rule
	    }
	    curNode = lub
	    idx += newNode.rule.length
	}

	return curNode.rule
    }
}
