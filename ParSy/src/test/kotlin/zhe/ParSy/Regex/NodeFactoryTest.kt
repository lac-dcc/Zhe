package zhe.ParSy.Regex

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class NodeFactoryTest {
    private val nf = NodeFactory()

    @Test
    fun parseNodesEmpty() {
        val s = ""
        assertThrows(Exception::class.java, {
            nf.parseNodes(s)
        })
    }


    @Test
    fun parseNodesA() {
        val s = "[a]{1,1}"
        val actual = nf.parseNodes(s)
        val expected = listOf<Node>(Node(setOf<Char>('a'), Pair(1.toUInt(), 1.toUInt())))
        assertEquals(expected, actual)
    }

    @Test
    fun parseNodesAB() {
        val s = "[ab]{1,1}"
        val actual = nf.parseNodes(s)
        val expected = listOf<Node>(Node(setOf<Char>('a', 'b'), Pair(1.toUInt(), 1.toUInt())))
        assertEquals(expected, actual)
    }

    @Test
    fun parseNodesKleene() {
        val s = "[ab]*"
        val actual = nf.parseNodes(s)
        val expected = listOf<Node>(Node(setOf<Char>('a', 'b'), Pair(0.toUInt(), 0.toUInt())))
        assertEquals(expected, actual)
    }


    @Test
    fun parseNodesMultipleNodes() {
        val s = "[ab]{1,1}[:]{1,1}"
        val actual = nf.parseNodes(s)
        val expected = listOf<Node>(
            Node(setOf<Char>('a', 'b'), Pair(1.toUInt(), 1.toUInt())),
            Node(setOf<Char>(':'), Pair(1.toUInt(), 1.toUInt())),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun parseNodesMultipleNodesWithKleene() {
        val s = "[ab]{1,1}[:]{1,1}[321]*"
        val actual = nf.parseNodes(s)
        val expected = listOf<Node>(
            Node(setOf<Char>('a', 'b'), Pair(1.toUInt(), 1.toUInt())),
            Node(setOf<Char>(':'), Pair(1.toUInt(), 1.toUInt())),
            Node(setOf<Char>('1', '2', '3'), Pair(0.toUInt(), 0.toUInt())),
        )
        assertEquals(expected, actual)
    }
}
