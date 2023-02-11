package zhe.ParSy.Merger

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import zhe.ParSy.Grammar.Rule
import zhe.ParSy.Grammar.Rule.TerminalRule
import zhe.ParSy.Regex.alphaStar
import zhe.ParSy.Regex.numStar
import zhe.ParSy.Regex.punctStar

public class HeapCNFMergerCompressTest {
    companion object {
        private val merger = HeapCNFMerger()
    }

    @Test
    fun oneRuleShouldNotBeChanged() {
        val inputRules = setOf<Rule>(
            TerminalRule("luhansk")
        )
        val expected = inputRules
        val actual = merger.compressRuleSet(inputRules)
        assertEquals(expected, actual)
    }

    @Test
    fun twoDisjunctRulesShouldNotBeChanged() {
        val inputRules = setOf<Rule>(
            TerminalRule("luhansk"),
            // The dot in the middle should make it impossible to compress with
            // previous rule.
            TerminalRule("ukraine.luhansk")
        )
        val expected = inputRules
        val actual = merger.compressRuleSet(inputRules)
        assertEquals(expected, actual)
    }

    @Test
    fun twoOverlappingRulesShouldBeCompressed() {
        val inputRules = setOf<Rule>(
            TerminalRule("kherson"),
            TerminalRule("ukraineKherson")
        )
        // Should be compressed into a single terminal rule.
        val expected = setOf<Rule>(
            TerminalRule(alphaStar)
        )
        val actual = merger.compressRuleSet(inputRules)
        assertEquals(expected, actual)
    }

    @Test
    fun multipleOverlappingRulesShouldBeCompressed() {
        val inputRules = setOf<Rule>(
            TerminalRule("luhansk"),
            TerminalRule("donetsk"),
            TerminalRule("zaporizhzhia"),
            TerminalRule("kherson")
        )
        // Should be compressed into a single terminal rule.
        val expected = setOf<Rule>(
            TerminalRule(alphaStar)
        )
        val actual = merger.compressRuleSet(inputRules)
        assertEquals(expected, actual)
    }

    @Test
    fun multipleIPAddressesShouldBecomeOneTest1() {
        val inputRules = setOf<Rule>(
            TerminalRule("1.2.3.4"),
            TerminalRule("12.34.56.78"),
            TerminalRule("123.456.789.123"),
            TerminalRule("321.123.321.123")
        )
        val expected = setOf<Rule>(
            TerminalRule("$numStar.$numStar.$numStar.$numStar")
        )
        val actual = merger.compressRuleSet(inputRules)
        assertEquals(expected, actual)
    }

    @Test
    fun multipleIPAddressesShouldBecomeOneTest2() {
        val inputRules = setOf<Rule>(
            TerminalRule("127.0.0.1"),
            TerminalRule("9.67.117.98"),
            TerminalRule("9.67.116.98"),
            TerminalRule("9.67.101.1"),
            TerminalRule("9.67.100.1"),
            TerminalRule("9.37.65.139"),
            TerminalRule("129.1.1.1")
        )
        val expected = setOf<Rule>(
            TerminalRule("$numStar.$numStar.$numStar.$numStar")
        )
        val actual = merger.compressRuleSet(inputRules)
        assertEquals(expected, actual)
    }

    @Test
    fun multipleIPAdressesWithNoise() {
        val inputRules = setOf<Rule>(
            TerminalRule("127.0.0.1,"),
            TerminalRule("9.67.117.98,"),
            TerminalRule("9.67.116.98,"),
            TerminalRule("9.67.101.1,"),
            TerminalRule("9.67.100.1,"),
            TerminalRule("9.37.65.139,"),
            TerminalRule("129.1.1.1,"),
            TerminalRule("Operation")
        )
        val expected = setOf<Rule>(
            TerminalRule("$numStar.$numStar.$numStar.$numStar,"),
            TerminalRule("Operation")
        )
        val actual = merger.compressRuleSet(inputRules)
        assertEquals(expected, actual)
    }

    @Test
    fun fuzzyWordsSomeWithPunctuation() {
        val inputRules = setOf<Rule>(
            TerminalRule("for"),
            TerminalRule("not"),
            TerminalRule("interface"),
            TerminalRule("returned"),
            TerminalRule("registration"),
            TerminalRule("with"),
            TerminalRule("TCP/IP"),
            TerminalRule("images"),
            TerminalRule("level"),
            TerminalRule("file:"),
            TerminalRule("Agent")
        )
        val expected = setOf<Rule>(
            TerminalRule(alphaStar),
            TerminalRule("$alphaStar$punctStar$alphaStar")
        )
        val actual = merger.compressRuleSet(inputRules)
        assertEquals(expected, actual)
    }
}
