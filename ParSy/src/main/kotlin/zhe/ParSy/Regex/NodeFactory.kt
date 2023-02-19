package zhe.ParSy.Regex

class NodeFactory {
    private var allNodes = mutableMapOf<String, Node>()

    fun build(rule: String, parents: Set<Node>, level: Int): Node {
        // TODO
        // val newNode = Node(rule, parents, level)
        // allNodes[rule] = newNode
        return dummyNode()
    }

    private fun buildBasicNodes(s: String): List<Node> {
        // Assume the entire string is just plain characters.
        //
        // Example
        // buildBasicNodes("ab") -> [Node([a]{1}), Node([b]{1})]
        //
        // Notice that the formatted string is 6x as long as the
        // non-formatted one.
        var nodes = listOf<Node>()
        s.forEach {
            nodes += Node(setOf(it), Pair(1.toUInt(), 1.toUInt()))
        }
        return nodes
    }

    private fun parseNodes(s: String): List<Node> {
        // TODO
        return listOf<Node>()
    }

    fun buildNodes(s: String): List<Node> {
        if (s.isEmpty()) {
            return listOf<Node>()
        }
        if (s[0] != '[') {
            // If the first character of the string is not a [, then it is
            // not formatted as we expect. In this case, we assume the
            // entire string is not formatted as expected and build basic
            // nodes from each of its characters.
            return buildBasicNodes(s)
        }
        return parseNodes(s)
    }

    fun buildString(nodes: List<Node>): String {
        var s = ""
        nodes.forEach {
            val cs = it.getCharset()
            val itvl = it.getInterval()
            if (it.isKleene()) {
                s += "[${collapseCharset(cs)}]*"
            } else {
                s += "[${collapseCharset(cs)}]{${itvl.first},${itvl.second}}"
            }
        }
        return s
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
