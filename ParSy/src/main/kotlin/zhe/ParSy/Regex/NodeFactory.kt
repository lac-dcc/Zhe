package zhe.ParSy.Regex

class NodeFactory {
    private var globalId: Int = 0
    private var allNodes = mutableMapOf<String, Node>()

    fun build(rule: String, parents: Set<Node>, level: Int): Node {
	val newNode = Node(globalId, rule, parents, level)
	globalId++
	allNodes[rule] = newNode
	return newNode
    }

    fun get(s: String): Node {
	return allNodes.getValue(s)
    }

    fun getByPrefix(s: String, sidx: Int): Node {
	return get(parseTokenPrefix(s, sidx))
    }

    fun getBySuffix(s: String, sidx: Int): Node {
	return get(parseTokenSuffix(s, sidx))
    }
}
