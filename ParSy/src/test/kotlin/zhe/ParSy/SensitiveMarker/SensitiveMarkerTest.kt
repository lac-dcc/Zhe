package zhe.ParSy.SensitiveMarker

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SensitiveMarkerTest {
    @Test
    fun findSensitiveWordsEmptyLine() {
	val line = ""
	val actual = findSensitiveWords(line)
	val expected = Pair(null, "")
	assertEquals(expected, actual)
    }

    // @Test
    // fun findSensitiveWordsLineWithoutMarkdown() {
    // 	val line = "Shaken, not stirred"
    // 	val actual = findSensitiveWords(line)
    // 	val expected = Pair(null, "Shaken, not stirred")
    // 	assertEquals(expected, actual)
    // }

    // @Test
    // fun findSensitiveWordsLineOneMarkedMiddle() {
    // 	val line = "My name is <s>Bond,</s> James Bond"
    // 	val actual = findSensitiveWords(line)
    // 	val expected = Pair(
    // 	    listOf(3),
    // 	    "My name is Bond, James Bond"
    // 	)
    // 	assertEquals(expected, actual)
    // }

    // @Test
    // fun findSensitiveWordsLineOneMarkedEnd() {
    // 	val line = "My name is Bond, James <s>Bond</s>"
    // 	val actual = findSensitiveWords(line)
    // 	val expected = Pair(
    // 	    listOf(5),
    // 	    "My name is Bond, James Bond"
    // 	)
    // 	assertEquals(expected, actual)
    // }

    // @Test
    // fun findSensitiveWordsLineOneMarkedMiddleTwoWords() {
    // 	val line = "My name is <s>Bond, James</s> Bond"
    // 	val actual = findSensitiveWords(line)
    // 	val expected = Pair(
    // 	    listOf(3, 4),
    // 	    "My name is Bond, James Bond"
    // 	)
    // 	assertEquals(expected, actual)
    // }

    // @Test
    // fun findSensitiveWordsLineOneMarkedEndTwoWords() {
    // 	val line = "My name is Bond, <s>James Bond</s>"
    // 	val actual = findSensitiveWords(line)
    // 	val expected = Pair(
    // 	    listOf(4, 5),
    // 	    "My name is Bond, James Bond"
    // 	)
    // 	assertEquals(expected, actual)
    // }

    // @Test
    // fun findSensitiveWordsLineOneMarkedEndThreeWords() {
    // 	val line = "My name is <s>Bond, James Bond</s>"
    // 	val actual = findSensitiveWords(line)
    // 	val expected = Pair(
    // 	    listOf(3, 4, 5),
    // 	    "My name is Bond, James Bond"
    // 	)
    // 	assertEquals(expected, actual)
    // }

    // @Test
    // fun findSensitiveWordsLineOneMarkedEndThreeWordsWithTrailingSpace() {
    // 	val line = "My name is <s>Bond, James Bond</s>  "
    // 	val actual = findSensitiveWords(line)
    // 	val expected = Pair(
    // 	    listOf(3, 4, 5),
    // 	    "My name is Bond, James Bond "
    // 	)
    // 	assertEquals(expected, actual)
    // }
}
