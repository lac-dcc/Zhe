package zhe.ParSy.Solver

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import zhe.ParSy.Factory.RuleFactory
import zhe.ParSy.Grammar.HeapCNFGrammar
import zhe.ParSy.Grammar.MutableRulesMap
import zhe.ParSy.Grammar.Rule.ProductionRule
import zhe.ParSy.Grammar.Rule.TerminalRule

public class TrivialSolverTest {
    companion object {
        private val solver = TrivialSolver()
    }

    @Test
    fun addTerminalRuleSensitiveAndNonSensitive() {
        val tokens = "My name is Bond".split(" ")
        val sensitiveTokensIndexes = setOf(3)
        val tokenStack = TokenStack(tokens, sensitiveTokensIndexes)
        val grammarRules = MutableRulesMap()
        val rFactory = RuleFactory(HeapCNFGrammar.maxSize(tokens.size))

        // The token here is "Bond", which we consider sensitive.
        val terminalRule1 = solver.addTerminalRule(
            tokenStack,
            grammarRules,
            rFactory
        )
        val expectedTerminalRule1 = ProductionRule(6, TerminalRule("Bond", true))
        assertEquals(expectedTerminalRule1, terminalRule1)
        assertEquals(3, tokenStack.size())
        assertEquals(1, grammarRules.size)

        val terminalRule2 = solver.addTerminalRule(
            tokenStack,
            grammarRules,
            rFactory
        )
        val expectedTerminalRule2 = ProductionRule(5, TerminalRule("is", false))
        assertEquals(expectedTerminalRule2, terminalRule2)
        assertEquals(2, tokenStack.size())
        assertEquals(2, grammarRules.size)
    }

    @Test
    fun solveNoSensitive() {
        val tokens = "I am Bond".split(" ")
        val rFactory = RuleFactory(HeapCNFGrammar.maxSize(tokens.size))
        val r4 = rFactory.getTerminalRule("Bond", false)
        val r3 = rFactory.getTerminalRule("am", false)
        val r2 = rFactory.getABRule(r3, r4)
        val r1 = rFactory.getTerminalRule("I", false)
        val r0 = rFactory.getABRule(r1, r2)
        val rmap = MutableRulesMap()
        rmap[0] = r0
        rmap[1] = r1
        rmap[2] = r2
        rmap[3] = r3
        rmap[4] = r4
        val expectedGrammar = HeapCNFGrammar(rmap)
        val sensitiveTokenIndexes = setOf<Int>()
        val actualGrammar = solver.solve(tokens, sensitiveTokenIndexes)
        assertEquals(expectedGrammar.rules, actualGrammar.rules)
    }

    @Test
    fun solveSensitive() {
        val tokens = "I am Bond".split(" ")
        val rFactory = RuleFactory(HeapCNFGrammar.maxSize(tokens.size))
        val r4 = rFactory.getTerminalRule("Bond", true)
        val r3 = rFactory.getTerminalRule("am", false)
        val r2 = rFactory.getABRule(r3, r4)
        val r1 = rFactory.getTerminalRule("I", false)
        val r0 = rFactory.getABRule(r1, r2)
        val rmap = MutableRulesMap()
        rmap[0] = r0
        rmap[1] = r1
        rmap[2] = r2
        rmap[3] = r3
        rmap[4] = r4
        val expectedGrammar = HeapCNFGrammar(rmap)
        val sensitiveTokenIndexes = setOf<Int>(2)
        val actualGrammar = solver.solve(tokens, sensitiveTokenIndexes)
        assertEquals(expectedGrammar.rules, actualGrammar.rules)
    }
}
