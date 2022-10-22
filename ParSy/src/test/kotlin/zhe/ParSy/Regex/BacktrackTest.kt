package zhe.ParSy.Regex

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BacktrackTest {
    private val lattice = Lattice()

    @Test
    fun backtrackSubIP() {
	val token = "123.456.789"
	val tokenIdx = 1

	val actual = lattice.backtrack(token, tokenIdx)
	val expected = Lattice.BacktrackResult("\\d*", 0, 3, lattice.top)

	assertThat(actual.regex).isEqualTo(expected.regex)
	assertThat(actual.left).isEqualTo(expected.left)
	assertThat(actual.right).isEqualTo(expected.right)
    }

    @Test
    fun backtrackProcessedTimestamp() {
	val token = "\\d*:\\d*:\\d*"
	val tokenIdx = 0

	val actual = lattice.backtrack(token, tokenIdx)
	val expected = Lattice.BacktrackResult("\\d*", 0, 3, lattice.top)

	assertThat(actual.regex).isEqualTo(expected.regex)
	assertThat(actual.left).isEqualTo(expected.left)
	assertThat(actual.right).isEqualTo(expected.right)
    }

}
