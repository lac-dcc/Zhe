package zhe.ParSy.Grammar

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import zhe.ParSy.Grammar.Rule.ABRule
import zhe.ParSy.Grammar.Rule.ProductionRule
import zhe.ParSy.Grammar.Rule.TerminalRule

public class RuleTest {
    @Test
    fun terminalRuleNotSensitiveToString() {
        val rule = TerminalRule("abc")
        assertEquals("<N>abc", rule.toString())
    }

    @Test
    fun terminalRuleSensitiveToString() {
        val rule = TerminalRule("abc", true)
        assertEquals("<S>abc", rule.toString())
    }

    @Test
    fun abRuleToString() {
        val rule = ABRule(1, 2)
        assertEquals("R1 R2", rule.toString())
    }

    @Test
    fun simpleProductionRuleToString() {
        val rule = ProductionRule(0, TerminalRule("abc"))
        assertEquals("R0 :: <N>abc", rule.toString())
    }

    @Test
    fun composedProductionRuleToString() {
        val childRules = setOf<Rule>(
            ABRule(1, 2),
            TerminalRule("abc"),
            TerminalRule("cba")
        )
        val rule = ProductionRule(0, childRules)
        assertEquals("R0 :: R1 R2 | <N>abc | <N>cba", rule.toString())
    }
}
