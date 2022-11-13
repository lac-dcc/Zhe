package zhe.ParSy.Regex

import kotlin.text.Regex

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

class PatternTest {
    @Test
    fun allAlphasHasCorrectNumberOfChars() {
	assertEquals(52, allAlphas.size)
    }

    @Test
    fun allNumsHasCorrectNumberOfChars() {
	assertEquals(10, allNums.size)
    }

    @Test
    fun starsAreParseable() {
	allStars.forEach { it ->
	    Regex(it)
	}
    }

    @Test
    fun starMatchesRightChars() {
	starsToChars.forEach { it ->
	    val r = Regex(it.key)
	    it.value.forEach { char ->
		assertTrue(r.matches(char))
	    }
	}
    }

    @Test
    fun starsAreDisjunct() {
	starsToChars.forEach { it ->
	    val r = Regex(it.key)
	    val allMinusStarChars = allChars - it.value
	    allMinusStarChars.forEach { char ->
		assertFalse(r.matches(char))
	    }
	}
    }
}
