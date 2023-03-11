package zhe.ParSy.Regex

import org.slf4j.LoggerFactory

class NodeFactory {
    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    private var allNodes = mutableMapOf<String, Node>()

    private val regexRegex = """\[([^\[\]]+)\]((\*)|(\{(\d+),(\d+)\}))""".toRegex()

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

    fun parseNodes(s: String): List<Node> {
        var matchResults = regexRegex.findAll(s)
        if (matchResults.count() == 0) {
            throw Exception("Malformed regex: no matches found in $s")
        }
        var nodes = listOf<Node>()
        matchResults.forEach {
            val matchedGroups = it.groupValues
            val charset = matchedGroups[1]
            val kleeneStar = matchedGroups[3]
            val rangeFirst = matchedGroups[5]
            logger.debug(
                "Charset: $charset. Kleene: $kleeneStar. " +
                    "Range first: $rangeFirst."
            )
            val rangeSecond = matchedGroups[6]
            logger.debug(
                "Charset: $charset. Kleene: $kleeneStar. " +
                    "Range first: $rangeFirst. Range second: $rangeSecond"
            )
            if (charset == "") {
                throw Exception("Malformed regex: charset must not be empty")
            }
            if (kleeneStar == "" && (rangeFirst == "" || rangeSecond == "")) {
                throw Exception(
                    "Malformed regex: either kleene star or regex " +
                        "range must be present"
                )
            }
            if (kleeneStar != "") {
                nodes += Node(charsetFromS(charset), Pair(0.toUInt(), 0.toUInt()))
            } else {
                nodes += Node(
                    charsetFromS(charset),
                    Pair(rangeFirst.toUInt(), rangeSecond.toUInt())
                )
            }
        }
        return nodes
    }

    fun buildNodes(s: String): List<Node> {
        if (s.isEmpty()) {
            return listOf<Node>()
        }
        if (s == dotStar) {
            return listOf(topNode())
        }
        if (s[0] != '[') {
            // If the first character of the string is not a [, then it is
            // not formatted as we expect. In this case, we assume the
            // entire string is not formatted as expected and build basic
            // nodes from each of its characters.
            //
            // TODO: improve parsing in cases like '[INFO] 2000-00-00 00:00:00
            //       This is my log'
            return buildBasicNodes(s)
        }
        return parseNodes(s)
    }

    fun buildString(nodes: List<Node>): String {
        // Special case for top node
        if (nodes.size == 1 && nodes[0].isTop) {
            return dotStar
        }

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
