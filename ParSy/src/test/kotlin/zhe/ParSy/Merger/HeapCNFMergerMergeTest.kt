package zhe.ParSy.Merger

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import zhe.ParSy.Solver.TrivialSolver

public class HeapCNFMergerMergeTest {
    companion object {
        private val merger = HeapCNFMerger()
        private val solver = TrivialSolver()
        private val fixtureExamples = listOf(
            "My name is bond, James Bond",
            "Received connection from 1.2.3.4:54321",
            "Received connection from 11.22.33.44:54321"
        )
        private val delim = " "
    }

    @Test
    fun mergeEmptyGrammars() {
        val grammar1 = solver.solve(listOf<String>(), setOf<Int>())
        val grammar2 = solver.solve(listOf<String>(), setOf<Int>())
        val expectedGrammar = grammar1
        val actualGrammar = merger.merge(grammar1, grammar2)
        assertEquals(expectedGrammar.toString(), actualGrammar.toString())
    }

    @Test
    fun mergeEmptyGrammarWithNonEmpty() {
        val grammar1 = solver.solve(listOf<String>(), setOf<Int>())
        val grammar2 = solver.solve(fixtureExamples[0].split(delim), setOf<Int>())
        val expectedGrammar = grammar2
        val actualGrammar = merger.merge(grammar1, grammar2)
        assertEquals(expectedGrammar.toString(), actualGrammar.toString())
    }

    @Test
    fun mergeTwoEqualGrammars() {
        val grammar1 = solver.solve(listOf<String>(), setOf<Int>())
        val grammar2 = grammar1
        val expectedGrammar = grammar1
        val actualGrammar = merger.merge(grammar1, grammar2)
        assertEquals(expectedGrammar.toString(), actualGrammar.toString())
    }

    @Test
    fun mergeTwoGrammarsSameSizeButDifferent() {
        val grammar1 = solver.solve(fixtureExamples[0].split(delim), setOf<Int>())
        val grammar2 = solver.solve(fixtureExamples[1].split(delim), setOf<Int>())
        val expectedGrammarString = """R0 :: R1 R2
R1 :: <N>\p{Alpha}*
R2 :: R3 R4
R3 :: <N>\p{Alpha}*
R4 :: R5 R6
R5 :: <N>\p{Alpha}*
R6 :: <N>1.2.3.4:54321 | R7 R8
R7 :: <N>bond,
R8 :: R9 R10
R9 :: <N>James
R10 :: <N>Bond
"""
        val actualGrammar = merger.merge(grammar1, grammar2)
        assertEquals(expectedGrammarString, actualGrammar.toString())
    }

    @Test
    fun mergeThreeGrammarsAccummulatively() {
        val grammar1 = solver.solve(fixtureExamples[0].split(delim), setOf<Int>())
        val grammar2 = solver.solve(fixtureExamples[1].split(delim), setOf<Int>())
        val grammar3 = solver.solve(fixtureExamples[2].split(delim), setOf<Int>())
        val expectedGrammarString = """R0 :: R1 R2
R1 :: <N>\p{Alpha}*
R2 :: R3 R4
R3 :: <N>\p{Alpha}*
R4 :: R5 R6
R5 :: <N>\p{Alpha}*
R6 :: <N>\d*.\d*.\d*.\d*:54321 | R7 R8
R7 :: <N>bond,
R8 :: R9 R10
R9 :: <N>James
R10 :: <N>Bond
"""
        var actualGrammar = merger.merge(grammar1, grammar2)
        actualGrammar = merger.merge(actualGrammar, grammar3)
        assertEquals(expectedGrammarString, actualGrammar.toString())
    }

    @Test
    fun mergeThreeGrammarsOneHasSensitiveToken() {
        val grammar1 = solver.solve(fixtureExamples[0].split(delim), setOf<Int>())
        val grammar2 = solver.solve(fixtureExamples[1].split(delim), setOf<Int>(3))
        val grammar3 = solver.solve(fixtureExamples[2].split(delim), setOf<Int>())
        val expectedGrammarString = """R0 :: R1 R2
R1 :: <N>\p{Alpha}*
R2 :: R3 R4
R3 :: <N>\p{Alpha}*
R4 :: R5 R6
R5 :: <N>\p{Alpha}*
R6 :: <S>\d*.\d*.\d*.\d*:54321 | R7 R8
R7 :: <N>bond,
R8 :: R9 R10
R9 :: <N>James
R10 :: <N>Bond
"""
        var actualGrammar = merger.merge(grammar1, grammar2)
        actualGrammar = merger.merge(actualGrammar, grammar3)
        assertEquals(expectedGrammarString, actualGrammar.toString())
    }
}
