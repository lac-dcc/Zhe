package zhe.ParSy.Regex

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

// TODO: add tests to test Inference Machine properties (iterative, set-driven,
// etc) -aholmquist 2022-10-22
class TransformTest {
    private val lattice = Lattice()

    @Test
    fun transformEmpty() {
    	val actual = lattice.transform("", "")
    	val expected = ""
    	assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun transformOneAlpha() {
    	val actual = lattice.transform("", "a")
    	val expected = "a"
    	assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun transformOneNum() {
    	val actual = lattice.transform("", "1")
    	val expected = "1"
    	assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun transformManyChar() {
    	val actual = lattice.transform("", "abcd")
    	val expected = "abcd"
    	assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun transformManyNum() {
    	val actual = lattice.transform("", "1234")
    	val expected = "1234"
    	assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun transformFromExistingSingletonEqual() {
    	val actual = lattice.transform("a", "a")
    	val expected = "a"
    	assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun transformFromExistingSingletonDiff() {
    	val actual = lattice.transform("a", "b")
    	val expected = "\\p{Alpha}*"
    	assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun transformFromExistingSingletonAlphaNum() {
    	val actual = lattice.transform("a", "1")
    	val expected = "\\p{Alnum}*"
    	assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun transformFromExistingMultipleRepeated() {
    	val actual = lattice.transform("aaaa", "aaaa")
    	val expected = "aaaa"
    	assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun transformFromExistingMultipleEqual() {
    	val actual = lattice.transform("abcd", "abcd")
    	val expected = "abcd"
    	assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun transformFromExistingMultipleEqualAlphaAndNum() {
    	val actual = lattice.transform("abc12", "abc12")
    	val expected = "abc12"
    	assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun transformFromExistingMultipleTwoDiffOneEqual() {
    	val actual = lattice.transform("cba", "abc")
    	val expected = "\\p{Alpha}*b\\p{Alpha}*"
    	assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun transformFromExistingMultipleAllDiff() {
    	val actual = lattice.transform("abc", "efg")
    	val expected = "\\p{Alpha}*"
    	assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun transformFromExistingMultipleDifferentSizes() {
    	val actual = lattice.transform("abc", "abcdef")
    	val expected = "abc\\p{Alpha}*"
    	assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun transformFromExistingMultipleDifferentSizesTwice() {
    	val actual1 = lattice.transform("abc", "abcdef")
    	val expected1 = "abc\\p{Alpha}*"
    	assertThat(actual1).isEqualTo(expected1)

    	val actual2 = lattice.transform(actual1, "abcdefghi")
    	val expected2 = "abc\\p{Alpha}*"
    	assertThat(actual2).isEqualTo(expected2)
    }

    @Test
    fun transformFromExistingPreviousIsLonger() {
    	val actual = lattice.transform("abcdef", "abc")
    	val expected = "abc\\p{Alpha}*"
    	assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun transformFromExistingMultipleAlphaNumDiff() {
    	val actual = lattice.transform("abc12", "efg34")
    	val expected = "\\p{Alpha}*\\d*"
    	assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun transformTimeStamp() {
    	val actual = lattice.transform("00:00:00", "12:34:56")
    	val expected = "\\d*:\\d*:\\d*"
    	assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun transformTimeStampTwice() {
    	var actual = lattice.transform("00:00:00", "12:34:56")
    	var expected = "\\d*:\\d*:\\d*"
    	assertThat(actual).isEqualTo(expected)

    	actual = lattice.transform(actual, "12:34:56")
    	// Regex should remain the same, given that we have already seen this
    	// example in the past.
    	assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun transformIP() {
    	var actual = lattice.transform("0.0.0.0", "12.34.56.78")
    	var expected = "\\d*.\\d*.\\d*.\\d*"
    	assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun transformSubIP() {
    	var actual = lattice.transform("1.2.3", "123.456.789")
    	var expected = "\\d*.\\d*.\\d*"
    	assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun transformIPAndSubIPCommonNumbers() {
    	var actual = lattice.transform("123.345.786", "12.34.78")
    	var expected = "\\d*.\\d*.78\\d*"
    	assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun transformIPAndSubIPCommonNumbersTwice() {
    	var actual = lattice.transform("123.345.786", "12.34.78")
    	var expected = "\\d*.\\d*.78\\d*"
    	assertThat(actual).isEqualTo(expected)

    	actual = lattice.transform(expected, "12.34.78")
    	expected = expected
    	assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun transformAlphaAndPunctSamePlace() {
    	var actual = lattice.transform("123a", "123:")
    	var expected = "\\p{Alnum}*\\p{Punct}*"
    	assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun transformTimestampAndNumber() {
    	var actual = lattice.transform("00:00:00", "123")
    	var expected = ".*"
    	assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun transformTimestampAndPartialTimestamp() {
    	var actual = lattice.transform("00:00:00", "00:00")
    	var expected = ".*"
    	assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun transformTimestampAndPartialTimestampReverseOrder() {
    	var actual = lattice.transform("00:00", "00:00:00")
    	var expected = ".*"
    	assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun transformPunctsDifferentSizes() {
    	var actual = lattice.transform("---", "====")
    	var expected = "\\p{Punct}*"
    	assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun transformPunctsDifferentSizesReverseOrder() {
    	var actual = lattice.transform("====", "---")
    	var expected = "\\p{Punct}*"
    	assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun transformDifferentAmountsOfPunctsBetweenAlphas() {
    	var actual = lattice.transform("ab.cd", "ab..cd")
    	var expected = "ab\\p{Punct}*cd"
    	assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun transformMissingPunct() {
    	var actual = lattice.transform("ab", "ab.")
    	var expected = "ab\\p{Punct}*"
    	assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun transformPunctWithAndWithoutAlphas() {
    	var actual = lattice.transform("ab.", "ab.a")
    	var expected = "ab.\\p{Alpha}*"
    	assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun transformAlphasAndAsterisk() {
	// Previously, this test case was resulting in an empty final regex.
    	var actual = lattice.transform("Specified", "*****")
    	var expected = ".*"
    	assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun transformPunctAndNumStar() {
	// Previously, this test case was resulting in an out of bounds
	// exception.
    	var actual = lattice.transform("#\\d*", "#\\d*")
    	var expected = "#\\d*"
    	assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun transformSingleNumAndMultipleAlpha() {
	// Previously, this test case was resulting in \p{Alnum}*\p{Alpha} . It
	// was a problem in the leftovers part.
    	var actual = lattice.transform("#\\d*", "#\\d*")
    	var expected = "#\\d*"
    	assertThat(actual).isEqualTo(expected)
    }

    // TODO: How to solve these cases :/ ? -aholmquist 2022-10-08
    // @Test
    // fun transformPunctWithAndWithoutAlphaAndDifferentAmountsOfPunct() {
    // 	var actual = lattice.transform("ab.", "ab..a")
    // 	var expected = "ab\\p{Punct}*\\p{Alpha}*"
    // 	assertThat(actual).isEqualTo(expected)
    // }
}
