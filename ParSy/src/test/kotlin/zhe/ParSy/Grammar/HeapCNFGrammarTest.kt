package zhe.ParSy.Grammar

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

import zhe.ParSy.Solver.TrivialSolver

public class HeapCNFGrammarTest {
    @Test
    fun rootIsRule0() {
        val tokens = "My name is Bond, James Bond".split(" ")
        val sensitiveTokenIndexes = setOf<Int>()
        val solver = TrivialSolver()
        val grammar = solver.solve(tokens, sensitiveTokenIndexes)
        val rootRule = grammar.root
        assertEquals(0, rootRule.id)
    }

}
