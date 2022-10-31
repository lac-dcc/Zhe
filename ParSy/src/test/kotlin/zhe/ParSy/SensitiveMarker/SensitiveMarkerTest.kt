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

    @Test
    fun findSensitiveWordsWithoutMarkdown() {
    	val line = "Shaken, not stirred"
    	val actual = findSensitiveWords(line)
    	val expected = Pair(null, "Shaken, not stirred")
    	assertEquals(expected, actual)
    }

    @Test
    fun findSensitiveWordsOneMarkedMiddle() {
    	val line = "My name is <s>Bond,</s> James Bond"
    	val actual = findSensitiveWords(line)
    	val expected = Pair(
    	    listOf(3),
    	    "My name is Bond, James Bond"
    	)
    	assertEquals(expected, actual)
    }

    @Test
    fun findSensitiveWordsOneMarkedEnd() {
    	val line = "My name is Bond, James <s>Bond</s>"
    	val actual = findSensitiveWords(line)
    	val expected = Pair(
    	    listOf(5),
    	    "My name is Bond, James Bond"
    	)
    	assertEquals(expected, actual)
    }

    @Test
    fun findSensitiveWordsTwoMarkedMiddle() {
    	val line = "My name is <s>Bond, James</s> Bond"
    	val actual = findSensitiveWords(line)
    	val expected = Pair(
    	    listOf(3, 4),
    	    "My name is Bond, James Bond"
    	)
    	assertEquals(expected, actual)
    }

    @Test
    fun findSensitiveWordsTwoMarkedEnd() {
    	val line = "My name is Bond, <s>James Bond</s>"
    	val actual = findSensitiveWords(line)
    	val expected = Pair(
    	    listOf(4, 5),
    	    "My name is Bond, James Bond"
    	)
    	assertEquals(expected, actual)
    }

    @Test
    fun findSensitiveWordsThreeMarkedEnd() {
    	val line = "My name is <s>Bond, James Bond</s>"
    	val actual = findSensitiveWords(line)
    	val expected = Pair(
    	    listOf(3, 4, 5),
    	    "My name is Bond, James Bond"
    	)
    	assertEquals(expected, actual)
    }

    @Test
    fun findSensitiveWordsThreeMarkedEndWithTrailingSpace() {
    	val line = "    My name is <s>Bond, James Bond</s>      "
    	val actual = findSensitiveWords(line)
    	val expected = Pair(
    	    listOf(3, 4, 5),
    	    "My name is Bond, James Bond"
    	)
    	assertEquals(expected, actual)
    }

    @Test
    fun findSensitiveWordsLotsOfSpaces() {
    	val line = "    My     name     is   <s>Bond,    James    Bond</s>     "
    	val actual = findSensitiveWords(line)
    	val expected = Pair(
    	    listOf(3, 4, 5),
    	    "My name is Bond, James Bond"
    	)
    	assertEquals(expected, actual)
    }
}
