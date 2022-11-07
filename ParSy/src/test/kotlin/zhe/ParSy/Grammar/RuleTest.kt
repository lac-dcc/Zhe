package zhe.ParSy.Grammar

import zhe.ParSy.Grammar.Rule.TerminalRule
import zhe.ParSy.Grammar.Rule.ABRule
import zhe.ParSy.Grammar.Rule.ProductionRule

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

public class RuleTest {
    @Test
    fun terminalRuleToString() {
	val rule = TerminalRule("abc")
	assertEquals("abc", rule.toString())
    }

    @Test
    fun abRuleToString() {
	val rule = ABRule(1, 2)
	assertEquals("R1 R2", rule.toString())
    }

    @Test
    fun abRuleSensitiveToString() {
	val rule = ABRule(1, 2, true, false)
	assertEquals("S1 R2", rule.toString())
    }

    @Test
    fun simpleProductionRuleToString() {
	val rule = ProductionRule(0, TerminalRule("abc"))
	assertEquals("R0 :: abc", rule.toString())
    }

    @Test
    fun simpleProductionRuleSensitiveToString() {
	val rule = ProductionRule(0, TerminalRule("abc"), true)
	assertEquals("S0 :: abc", rule.toString())
    }

    @Test
    fun composedProductionRuleToString() {
	val childRules = setOf<Rule>(
	    ABRule(1, 2),
	    TerminalRule("abc"),
	    TerminalRule("cba"),
	)
	val rule = ProductionRule(0, childRules, false)
	assertEquals("R0 :: R1 R2 | abc | cba", rule.toString())
    }

    @Test
    fun composedProductionRuleAllSensitiveToString() {
	val childRules = setOf<Rule>(
	    ABRule(1, 2, true, true),
	    TerminalRule("abc", true),
	    TerminalRule("cba", true),
	)
	val rule = ProductionRule(0, childRules, true)
	assertEquals("S0 :: S1 S2 | abc | cba", rule.toString())
    }
}
