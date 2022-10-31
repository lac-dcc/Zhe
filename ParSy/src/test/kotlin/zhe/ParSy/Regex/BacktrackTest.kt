package zhe.ParSy.Regex

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BacktrackTest {
    private val lattice = Lattice()

    @Test
    fun backtrackSubIP() {
	val token = "123.456.789"
	val tokenIdx = 1

	val actual = lattice.backtrack(token, tokenIdx)
	val expected = Lattice.BacktrackResult("\\d*", 0, 3, lattice.top)

	assertEquals(expected.regex, actual.regex)
	assertEquals(expected.left, actual.left)
	assertEquals(expected.right, actual.right)
    }

    @Test
    fun backtrackProcessedTimestamp() {
	val token = "\\d*:\\d*:\\d*"
	val tokenIdx = 0

	val actual = lattice.backtrack(token, tokenIdx)
	val expected = Lattice.BacktrackResult("\\d*", 0, 3, lattice.top)

	assertEquals(expected.regex, actual.regex)
	assertEquals(expected.left, actual.left)
	assertEquals(expected.right, actual.right)
    }

}
