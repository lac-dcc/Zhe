package zhe.ParSy.Grammar

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import zhe.ParSy.Solver.TrivialSolver

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

    @Test
    fun grammarToStr() {
        val tokens = "Hello World".split(" ")
        val sensitiveTokenIndexes = setOf<Int>()
        val solver = TrivialSolver()
        val grammar = solver.solve(tokens, sensitiveTokenIndexes)
        assertEquals(
            """grammar ZheGrammar;

entrypoint: nonSensitive0;
nonSensitive0: NON_SENSITIVE1 NON_SENSITIVE2;
NON_SENSITIVE1: 'Hello';
NON_SENSITIVE2: 'World';
""",
            grammar.toString()
        )
    }
}
