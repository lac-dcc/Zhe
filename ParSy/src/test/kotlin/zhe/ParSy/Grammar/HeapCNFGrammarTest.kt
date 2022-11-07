package zhe.ParSy.Grammar

import zhe.ParSy.Solver.TrivialSolver

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

public class HeapCNFGrammarTest {
    @Test
    fun rootIsR0() {
	val tokens = "My name is Bond, James Bond".split(" ")
	val sensitiveTokenIndexes = setOf<Int>()
	val solver = TrivialSolver()
	val grammar = solver.solve(tokens, sensitiveTokenIndexes)
	val rootRule = grammar.root
	assertTrue(rootRule.toString().startsWith("R0"))
    }
}
