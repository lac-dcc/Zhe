package zhe.ParSy.Regex

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// TODO: add tests to test Inference Machine properties (iterative, set-driven,
// etc) -aholmquist 2022-10-22
class CompressorTest {
    private val compressor = Compressor(NodeFactory())

    @Test
    fun compressEmpty() {
        val actual = compressor.compressToString("", "")
        val expected = ""
        assertEquals(expected, actual)
    }

    @Test
    fun compressOneAlpha() {
        val actual = compressor.compressToString("", "a")
        val expected = "a"
        assertEquals(expected, actual)
    }

    @Test
    fun compressOneNum() {
        val actual = compressor.compressToString("", "1")
        val expected = "1"
        assertEquals(expected, actual)
    }

    @Test
    fun compressManyChar() {
        val actual = compressor.compressToString("", "abcd")
        val expected = "abcd"
        assertEquals(expected, actual)
    }

    @Test
    fun compressManyNum() {
        val actual = compressor.compressToString("", "1234")
        val expected = "1234"
        assertEquals(expected, actual)
    }

    @Test
    fun compressFromExistingSingletonEqual() {
        val actual = compressor.compressToString("a", "a")
        val expected = "a"
        assertEquals(expected, actual)
    }

    @Test
    fun compressFromExistingSingletonDiff() {
        val actual = compressor.compressToString("a", "b")
        val expected = "\\p{Alpha}*"
        assertEquals(expected, actual)
    }

    @Test
    fun compressFromExistingSingletonAlphaNum() {
        val actual = compressor.compressToString("a", "1")
        val expected = "\\p{Alnum}*"
        assertEquals(expected, actual)
    }

    @Test
    fun compressFromExistingMultipleRepeated() {
        val actual = compressor.compressToString("aaaa", "aaaa")
        val expected = "aaaa"
        assertEquals(expected, actual)
    }

    @Test
    fun compressFromExistingMultipleEqual() {
        val actual = compressor.compressToString("abcd", "abcd")
        val expected = "abcd"
        assertEquals(expected, actual)
    }

    @Test
    fun compressFromExistingMultipleEqualAlphaAndNum() {
        val actual = compressor.compressToString("abc12", "abc12")
        val expected = "abc12"
        assertEquals(expected, actual)
    }

    @Test
    fun compressFromExistingMultipleTwoDiffOneEqual() {
        val actual = compressor.compressToString("cba", "abc")
        val expected = "\\p{Alpha}*b\\p{Alpha}*"
        assertEquals(expected, actual)
    }

    @Test
    fun compressFromExistingMultipleAllDiff() {
        val actual = compressor.compressToString("abc", "efg")
        val expected = "\\p{Alpha}*"
        assertEquals(expected, actual)
    }

    @Test
    fun compressFromExistingMultipleDifferentSizes() {
        val actual = compressor.compressToString("abc", "abcdef")
        val expected = "abc\\p{Alpha}*"
        assertEquals(expected, actual)
    }

    @Test
    fun compressFromExistingMultipleDifferentSizesTwice() {
        val actual1 = compressor.compressToString("abc", "abcdef")
        val expected1 = "abc\\p{Alpha}*"
        assertEquals(expected1, actual1)

        val actual2 = compressor.compressToString(actual1, "abcdefghi")
        val expected2 = "abc\\p{Alpha}*"
        assertEquals(expected2, actual2)
    }

    @Test
    fun compressFromExistingPreviousIsLonger() {
        val actual = compressor.compressToString("abcdef", "abc")
        val expected = "abc\\p{Alpha}*"
        assertEquals(expected, actual)
    }

    @Test
    fun compressFromExistingMultipleAlphaNumDiff() {
        val actual = compressor.compressToString("abc12", "efg34")
        val expected = "\\p{Alpha}*\\d*"
        assertEquals(expected, actual)
    }

    @Test
    fun compressTimeStamp() {
        val actual = compressor.compressToString("00:00:00", "12:34:56")
        val expected = "\\d*:\\d*:\\d*"
        assertEquals(expected, actual)
    }

    @Test
    fun compressTimeStampTwice() {
        var actual = compressor.compressToString("00:00:00", "12:34:56")
        var expected = "\\d*:\\d*:\\d*"
        assertEquals(expected, actual)

        actual = compressor.compressToString(actual, "12:34:56")
        // Regex should remain the same, given that we have already seen this
        // example in the past.
        assertEquals(expected, actual)
    }

    @Test
    fun compressIP() {
        var actual = compressor.compressToString("0.0.0.0", "12.34.56.78")
        var expected = "\\d*.\\d*.\\d*.\\d*"
        assertEquals(expected, actual)
    }

    @Test
    fun compressSubIP() {
        var actual = compressor.compressToString("1.2.3", "123.456.789")
        var expected = "\\d*.\\d*.\\d*"
        assertEquals(expected, actual)
    }

    @Test
    fun compressIPAndSubIPCommonNumbers() {
        var actual = compressor.compressToString("123.345.786", "12.34.78")
        var expected = "\\d*.\\d*.78\\d*"
        assertEquals(expected, actual)
    }

    @Test
    fun compressIPAndSubIPCommonNumbersTwice() {
        var actual = compressor.compressToString("123.345.786", "12.34.78")
        var expected = "\\d*.\\d*.78\\d*"
        assertEquals(expected, actual)

        actual = compressor.compressToString(expected, "12.34.78")
        expected = expected
        assertEquals(expected, actual)
    }

    @Test
    fun compressAlphaAndPunctSamePlace() {
        var actual = compressor.compressToString("123a", "123:")
        var expected = "\\p{Alnum}*\\p{Punct}*"
        assertEquals(expected, actual)
    }

    @Test
    fun compressTimestampAndNumber() {
        var actual = compressor.compressToString("00:00:00", "123")
        var expected = ".*"
        assertEquals(expected, actual)
    }

    @Test
    fun compressTimestampAndPartialTimestamp() {
        var actual = compressor.compressToString("00:00:00", "00:00")
        var expected = ".*"
        assertEquals(expected, actual)
    }

    @Test
    fun compressTimestampAndPartialTimestampReverseOrder() {
        var actual = compressor.compressToString("00:00", "00:00:00")
        var expected = ".*"
        assertEquals(expected, actual)
    }

    @Test
    fun compressPunctsDifferentSizes() {
        var actual = compressor.compressToString("---", "====")
        var expected = "\\p{Punct}*"
        assertEquals(expected, actual)
    }

    @Test
    fun compressPunctsDifferentSizesReverseOrder() {
        var actual = compressor.compressToString("====", "---")
        var expected = "\\p{Punct}*"
        assertEquals(expected, actual)
    }

    @Test
    fun compressDifferentAmountsOfPunctsBetweenAlphas() {
        var actual = compressor.compressToString("ab.cd", "ab..cd")
        var expected = "ab\\p{Punct}*cd"
        assertEquals(expected, actual)
    }

    @Test
    fun compressMissingPunct() {
        var actual = compressor.compressToString("ab", "ab.")
        var expected = "ab\\p{Punct}*"
        assertEquals(expected, actual)
    }

    @Test
    fun compressPunctWithAndWithoutAlphas() {
        var actual = compressor.compressToString("ab.", "ab.a")
        var expected = "ab.\\p{Alpha}*"
        assertEquals(expected, actual)
    }

    @Test
    fun compressAlphasAndAsterisk() {
        // Previously, this test case was resulting in an empty final regex.
        var actual = compressor.compressToString("Specified", "*****")
        var expected = ".*"
        assertEquals(expected, actual)
    }

    @Test
    fun compressPunctAndNumStar() {
        // Previously, this test case was resulting in an out of bounds
        // exception.
        var actual = compressor.compressToString("#\\d*", "#\\d*")
        var expected = "#\\d*"
        assertEquals(expected, actual)
    }

    @Test
    fun compressSingleNumAndMultipleAlpha() {
        // Previously, this test case was resulting in \p{Alnum}*\p{Alpha} . It
        // was a problem in the leftovers part.
        var actual = compressor.compressToString("7", "system")
        var expected = "\\p{Alnum}*"
        assertEquals(expected, actual)
    }

    @Test
    fun compressWordsAndPunctuationMixed() {
        var actual = compressor.compressToString("word1", "word2")
        actual = compressor.compressToString(actual, "word3")
        actual = compressor.compressToString(actual, "---")
        actual = compressor.compressToString(actual, "==")
        var expected = ".*"
        assertEquals(expected, actual)
    }

    @Test
    fun compressTokenIsRegex() {
        var actual = compressor.compressToString("123.123.123.123", "\\d*.\\d*.\\d*.4\\d*")
        var expected = "\\d*.\\d*.\\d*.\\d*"
        assertEquals(expected, actual)
    }

    @Test
    fun compressCrazyPunctuationSpam() {
        var actual = compressor.compressToString("12312-3-1=231-=3", "111-=-12=-312-23-=")
        var expected = "\\d*\\p{Punct}*\\d*\\p{Punct}*\\d*\\p{Punct}*\\d*-=\\d*"
        assertEquals(expected, actual)
    }

    // // TODO: How to solve these cases :/ ? -aholmquist 2022-10-08
    // @Test
    // fun compressPunctWithAndWithoutAlphaAndDifferentAmountsOfPunct() {
    // 	var actual = compressor.compressToString("ab.", "ab..a")
    // 	var expected = "ab\\p{Punct}*\\p{Alpha}*"
    // 	assertEquals(expected, actual)
    // }

    @Test
    fun backtrackSubIP() {
        val token = "123.456.789"
        val tokenIdx = 1

        val actual = compressor.backtrack(token, tokenIdx)
        val expected = Compressor.BacktrackResult("\\d*", 0, 3, dummyNode())

        assertEquals(expected.regex, actual.regex)
        assertEquals(expected.left, actual.left)
        assertEquals(expected.right, actual.right)
    }

    @Test
    fun backtrackProcessedTimestamp() {
        val token = "\\d*:\\d*:\\d*"
        val tokenIdx = 0

        val actual = compressor.backtrack(token, tokenIdx)
        val expected = Compressor.BacktrackResult("\\d*", 0, 3, dummyNode())

        assertEquals(expected.regex, actual.regex)
        assertEquals(expected.left, actual.left)
        assertEquals(expected.right, actual.right)
    }
}
