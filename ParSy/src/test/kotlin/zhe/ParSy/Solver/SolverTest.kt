package zhe.ParSy.Solver

import zhe.ParSy.Factory.RuleFactory
import zhe.ParSy.Grammar.Rule.ProductionRule
import zhe.ParSy.Grammar.Rule.TerminalRule
import zhe.ParSy.Grammar.HeapCNFGrammar
import zhe.ParSy.Grammar.MutableRulesMap

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

public class SolverTest {
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
	val terminalRule1 = solver.addTerminalRule(tokenStack, grammarRules,
						   rFactory)
	val expectedTerminalRule1 = ProductionRule(6, TerminalRule("Bond", true))
	assertEquals(expectedTerminalRule1, terminalRule1)
	assertEquals(3, tokenStack.size())
	assertEquals(1, grammarRules.size)

	val terminalRule2 = solver.addTerminalRule(tokenStack, grammarRules,
						   rFactory)
	val expectedTerminalRule2 = ProductionRule(5, TerminalRule("is", false))
	assertEquals(expectedTerminalRule2, terminalRule2)
	assertEquals(2, tokenStack.size())
	assertEquals(2, grammarRules.size)
    }

    @Test
    fun solveNoSensitive() {
	val tokens = "I am Bond".split(" ")
	val sensitiveTokenIndexes = setOf<Int>()
	val expectedGrammarString = """R4 :: <N>Bond
R3 :: <N>am
R2 :: R3 R4
R1 :: <N>I
R0 :: R1 R2
"""
	val actualGrammar = solver.solve(tokens, sensitiveTokenIndexes)
	val actualGrammarString = actualGrammar.toString()
	assertEquals(expectedGrammarString, actualGrammarString)
    }

    @Test
    fun solveSensitive() {
	val tokens = "I am Bond".split(" ")
	val sensitiveTokenIndexes = setOf<Int>(2)
	val expectedGrammarString = """R4 :: <S>Bond
R3 :: <N>am
R2 :: R3 R4
R1 :: <N>I
R0 :: R1 R2
"""
	val actualGrammar = solver.solve(tokens, sensitiveTokenIndexes)
	val actualGrammarString = actualGrammar.toString()
	assertEquals(expectedGrammarString, actualGrammarString)
    }
}
