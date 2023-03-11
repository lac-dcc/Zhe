package zhe.ParSy.Regex

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// TODO: add tests to test Inference Machine properties (iterative, set-driven,
// etc) -aholmquist 2022-10-22
class CompressorTest {
    private val nf = NodeFactory()
    private val compressor = Compressor(
        nf,
        testBaseNodes(),
        testDisjointNodes()
    )

    @Test
    fun formatNodesEmpty() {
        val tokens = listOf<Node>()
        val actual = compressor.formatNodes(tokens)
        val expected = tokens
        assertEquals(expected, actual)
    }

    @Test
    fun formatNodesOneNode() {
        val tokens = nf.buildNodes("a")
        val actual = compressor.formatNodes(tokens)
        val expected = tokens
        assertEquals(expected, actual)
    }

    @Test
    fun formatNodesTwoNodesIntoOne() {
        val tokens = nf.buildNodes("ab")
        val actual = compressor.formatNodes(tokens)
        val expected = listOf<Node>(
            Node(
                setOf<Char>('a', 'b'),
                Pair(2.toUInt(), 2.toUInt())
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun formatNodesThreeNodesIntoOneNums() {
        val tokens = nf.buildNodes("123")
        val actual = compressor.formatNodes(tokens)
        val expected = listOf<Node>(
            Node(
                setOf<Char>('1', '2', '3'),
                Pair(3.toUInt(), 3.toUInt())
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun formatNodesTop() {
        val tokens = nf.buildNodes("a:b")
        val actual = compressor.formatNodes(tokens)
        val expected = listOf<Node>(
            Node(setOf<Char>('a'), Pair(1.toUInt(), 1.toUInt())),
            Node(setOf<Char>(':'), Pair(1.toUInt(), 1.toUInt())),
            Node(setOf<Char>('b'), Pair(1.toUInt(), 1.toUInt()))
        )
        assertEquals(expected, actual)
    }

    @Test
    fun formatNodesIP() {
        val tokens = nf.buildNodes("12.23.34.45")
        val actual = compressor.formatNodes(tokens)
        val expected = listOf<Node>(
            Node(setOf<Char>('1', '2'), Pair(2.toUInt(), 2.toUInt())),
            Node(setOf<Char>('.'), Pair(1.toUInt(), 1.toUInt())),
            Node(setOf<Char>('2', '3'), Pair(2.toUInt(), 2.toUInt())),
            Node(setOf<Char>('.'), Pair(1.toUInt(), 1.toUInt())),
            Node(setOf<Char>('3', '4'), Pair(2.toUInt(), 2.toUInt())),
            Node(setOf<Char>('.'), Pair(1.toUInt(), 1.toUInt())),
            Node(setOf<Char>('4', '5'), Pair(2.toUInt(), 2.toUInt()))
        )
        assertEquals(expected, actual)
    }

    @Test
    fun compressEmpty() {
        val actual = compressor.compressToString("", "")
        val expected = ""
        assertEquals(expected, actual)
    }

    @Test
    fun compressOneAlpha() {
        val actual = compressor.compressToString("", "a")
        val expected = "[a]{1,1}"
        assertEquals(expected, actual)
    }

    @Test
    fun compressOneNum() {
        val actual = compressor.compressToString("", "1")
        val expected = "[1]{1,1}"
        assertEquals(expected, actual)
    }

    @Test
    fun compressManyChar() {
        val actual = compressor.compressToString("", "abcd")
        val expected = "[abcd]{4,4}"
        assertEquals(expected, actual)
    }

    @Test
    fun compressManyNum() {
        val actual = compressor.compressToString("", "1234")
        val expected = "[1234]{4,4}"
        assertEquals(expected, actual)
    }

    @Test
    fun compressFromExistingSingletonEqual() {
        val actual = compressor.compressToString("a", "a")
        val expected = "[a]{1,1}"
        assertEquals(expected, actual)
    }

    @Test
    fun compressFromExistingSingletonDiff() {
        val actual = compressor.compressToString("a", "b")
        val expected = "[ab]{1,1}"
        assertEquals(expected, actual)
    }

    @Test
    fun compressFromExistingSingletonAlphaNum() {
        val actual = compressor.compressToString("a", "1")
        val expected = "[0123456789abcdefghijklmnopqrstuvwxyz]*"
        assertEquals(expected, actual)
    }

    @Test
    fun compressFromExistingMultipleRepeated() {
        val actual = compressor.compressToString("aaaa", "aaaa")
        val expected = "[a]{4,4}"
        assertEquals(expected, actual)
    }

    @Test
    fun compressFromExistingMultipleEqual() {
        val actual = compressor.compressToString("abcd", "abcd")
        val expected = "[abcd]{4,4}"
        assertEquals(expected, actual)
    }

    @Test
    fun compressFromExistingMultipleEqualAlphaAndNum() {
        val actual = compressor.compressToString("abc12", "abc12")
        val expected = "[0123456789abcdefghijklmnopqrstuvwxyz]*"
        assertEquals(expected, actual)
    }

    @Test
    fun compressFromExistingMultipleTwoDiffOneEqual() {
        val actual = compressor.compressToString("cba", "abc")
        val expected = "[abc]{3,3}"
        assertEquals(expected, actual)
    }

    @Test
    fun compressFromExistingMultipleAllDiff() {
        val actual = compressor.compressToString("abc", "efg")
        val expected = "[abcefg]{3,3}"
        assertEquals(expected, actual)
    }

    @Test
    fun compressFromExistingMultipleDifferentSizes() {
        val actual = compressor.compressToString("abc", "abcdef")
        val expected = "[abcdef]{3,6}"
        assertEquals(expected, actual)
    }

    @Test
    fun compressFromExistingMultipleDifferentSizesTwice() {
        val actual1 = compressor.compressToString("abc", "abcdef")
        val expected1 = "[abcdef]{3,6}"
        assertEquals(expected1, actual1)

        val actual2 = compressor.compressToString(actual1, "abcdefghi")
        val expected2 = "[abcdefghi]{3,9}"
        assertEquals(expected2, actual2)
    }

    @Test
    fun compressFromExistingPreviousIsLonger() {
        val actual = compressor.compressToString("abcdef", "abc")
        val expected = "[abcdef]{3,6}"
        assertEquals(expected, actual)
    }

    @Test
    fun compressFromExistingMultipleAlphaNumDiff() {
        val actual = compressor.compressToString("abc12", "efg34")
        val expected = "[0123456789abcdefghijklmnopqrstuvwxyz]*"
        assertEquals(expected, actual)
    }

    @Test
    fun compressTimestamp() {
        val actual = compressor.compressToString("00:00:00", "12:34:56")
        val expected = "[012]{2,2}[:]{1,1}[034]{2,2}[:]{1,1}[056]{2,2}"
        assertEquals(expected, actual)
    }

    @Test
    fun compressTimestampTwice() {
        var actual = compressor.compressToString("00:00:00", "12:34:56")
        var expected = "[012]{2,2}[:]{1,1}[034]{2,2}[:]{1,1}[056]{2,2}"
        assertEquals(expected, actual)

        actual = compressor.compressToString(actual, "12:34:56")
        // Regex should remain the same, given that we have already seen this
        // example in the past.
        assertEquals(expected, actual)
    }

    @Test
    fun compressIP() {
        var actual = compressor.compressToString("0.0.0.0", "12.34.56.78")
        var expected = "[012]{1,2}[.]{1,1}[034]{1,2}[.]{1,1}[056]{1,2}[.]{1,1}[078]{1,2}"
        assertEquals(expected, actual)
    }

    @Test
    fun compressSubIP() {
        var actual = compressor.compressToString("1.2.3", "123.456.789")
        var expected = "[123]{1,3}[.]{1,1}[2456]{1,3}[.]{1,1}[3789]{1,3}"
        assertEquals(expected, actual)
    }

    @Test
    fun compressIPAndSubIPCommonNumbers() {
        var actual = compressor.compressToString("123.345.786", "12.34.78")
        var expected = "[123]{2,3}[.]{1,1}[345]{2,3}[.]{1,1}[678]{2,3}"
        assertEquals(expected, actual)
    }

    @Test
    fun compressIPAndSubIPCommonNumbersTwice() {
        var actual = compressor.compressToString("123.345.786", "12.34.78")
        var expected = "[123]{2,3}[.]{1,1}[345]{2,3}[.]{1,1}[678]{2,3}"
        assertEquals(expected, actual)

        // Shouldn't change
        actual = compressor.compressToString(expected, "12.34.78")
        assertEquals(expected, actual)
    }

    @Test
    fun compressAlphaAndPunctSamePlace() {
        var actual = compressor.compressToString("123a", "123:")
        var expected = ".*"
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
        var expected = "[-=]{3,4}"
        assertEquals(expected, actual)
    }

    @Test
    fun compressPunctsDifferentSizesReverseOrder() {
        var actual = compressor.compressToString("====", "---")
        var expected = "[-=]{3,4}"
        assertEquals(expected, actual)
    }

    @Test
    fun compressDifferentAmountsOfPunctsBetweenAlphas() {
        var actual = compressor.compressToString("ab.cd", "ab..cd")
        var expected = "[ab]{2,2}[.]{1,2}[cd]{2,2}"
        assertEquals(expected, actual)
    }

    @Test
    fun compressMissingPunct() {
        var actual = compressor.compressToString("ab", "ab.")
        var expected = ".*"
        assertEquals(expected, actual)
    }

    @Test
    fun compressPunctWithAndWithoutAlphas() {
        var actual = compressor.compressToString("ab.", "ab.a")
        var expected = ".*"
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
    fun compressSquareBracketsAndOtherPunct() {
        var actual = compressor.compressToString("#[", "#]")
        var expected = "[#\\[\\]]{2,2}"
        assertEquals(expected, actual)
    }

    @Test
    fun compressSingleNumAndMultipleAlpha() {
        var actual = compressor.compressToString("7", "system")
        var expected = "[0123456789abcdefghijklmnopqrstuvwxyz]*"
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

    // @Test
    // fun compressTokenIsRegex() {
    //     var actual = compressor.compressToString("123.123.123.123", "$numStar.$numStar.$numStar.4$numStar")
    //     var expected = "$numStar.$numStar.$numStar.$numStar"
    //     assertEquals(expected, actual)
    // }

    // @Test
    // fun compressCrazyPunctuationSpam() {
    //     var actual = compressor.compressToString("12312-3-1=231-=3", "111-=-12=-312-23-=")
    //     var expected = "${numStar}${punctStar}${numStar}${punctStar}${numStar}${punctStar}$numStar-=$numStar"
    //     assertEquals(expected, actual)
    // }

    // // TODO: How to solve these cases :/ ? -aholmquist 2022-10-08
    // @Test
    // fun compressPunctWithAndWithoutAlphaAndDifferentAmountsOfPunct() {
    // 	var actual = compressor.compressToString("ab.", "ab..a")
    // 	var expected = "ab${punctStar}${alphaStar}"
    // 	assertEquals(expected, actual)
    // }
}
