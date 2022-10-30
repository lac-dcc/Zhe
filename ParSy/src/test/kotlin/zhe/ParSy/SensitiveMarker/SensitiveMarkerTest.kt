package zhe.ParSy.SensitiveMarker

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TestSensitiveMarker {
    @Test
    fun findSensitiveWordsTestEmptyLine() {
	val line = ""
	val actual = findSensitiveWords(line)
	val expected = Pair(null, "")
	assertThat(actual).isEqualTo(expected)
    }
}
