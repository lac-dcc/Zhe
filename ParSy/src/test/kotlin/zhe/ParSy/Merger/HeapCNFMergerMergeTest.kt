package zhe.ParSy.Merger

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import zhe.ParSy.Factory.RuleFactory
import zhe.ParSy.Grammar.HeapCNFGrammar
import zhe.ParSy.Grammar.MutableRulesMap
import zhe.ParSy.Grammar.Rule
import zhe.ParSy.Grammar.Rule.ABRule
import zhe.ParSy.Grammar.Rule.TerminalRule
import zhe.ParSy.Regex.alphaStar
import zhe.ParSy.Regex.numStar
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
        val rFactory = RuleFactory(11)
        val r10 = rFactory.getTerminalRule("Bond", false)
        val r9 = rFactory.getTerminalRule("James", false)
        val r8 = rFactory.getABRule(r9, r10)
        val r7 = rFactory.getTerminalRule("bond,", false)
        val r6 = rFactory.getProductionRule(
            setOf<Rule>(
                TerminalRule("1.2.3.4:54321"),
                ABRule(7, 8)
            )
        )
        val r5 = rFactory.getTerminalRule("$alphaStar", false)
        val r4 = rFactory.getABRule(r5, r6)
        val r3 = rFactory.getTerminalRule("$alphaStar", false)
        val r2 = rFactory.getABRule(r3, r4)
        val r1 = rFactory.getTerminalRule("$alphaStar", false)
        val r0 = rFactory.getABRule(r1, r2)
        val rmap = MutableRulesMap()
        rmap[0] = r0
        rmap[1] = r1
        rmap[2] = r2
        rmap[3] = r3
        rmap[4] = r4
        rmap[5] = r5
        rmap[6] = r6
        rmap[7] = r7
        rmap[8] = r8
        rmap[9] = r9
        rmap[10] = r10
        val expectedGrammar = HeapCNFGrammar(rmap)
        val actualGrammar = merger.merge(grammar1, grammar2)
        assertEquals(expectedGrammar.rules, actualGrammar.rules)
    }

    @Test
    fun mergeThreeGrammarsAccummulatively() {
        val grammar1 = solver.solve(fixtureExamples[0].split(delim), setOf<Int>())
        val grammar2 = solver.solve(fixtureExamples[1].split(delim), setOf<Int>())
        val grammar3 = solver.solve(fixtureExamples[2].split(delim), setOf<Int>())
        val rFactory = RuleFactory(11)
        val r10 = rFactory.getTerminalRule("Bond", false)
        val r9 = rFactory.getTerminalRule("James", false)
        val r8 = rFactory.getABRule(r9, r10)
        val r7 = rFactory.getTerminalRule("bond,", false)
        val r6 = rFactory.getProductionRule(
            setOf<Rule>(
                TerminalRule("$numStar.$numStar.$numStar.$numStar:54321"),
                ABRule(7, 8)
            )
        )
        val r5 = rFactory.getTerminalRule("$alphaStar", false)
        val r4 = rFactory.getABRule(r5, r6)
        val r3 = rFactory.getTerminalRule("$alphaStar", false)
        val r2 = rFactory.getABRule(r3, r4)
        val r1 = rFactory.getTerminalRule("$alphaStar", false)
        val r0 = rFactory.getABRule(r1, r2)
        val rmap = MutableRulesMap()
        rmap[0] = r0
        rmap[1] = r1
        rmap[2] = r2
        rmap[3] = r3
        rmap[4] = r4
        rmap[5] = r5
        rmap[6] = r6
        rmap[7] = r7
        rmap[8] = r8
        rmap[9] = r9
        rmap[10] = r10
        val expectedGrammar = HeapCNFGrammar(rmap)
        var actualGrammar = merger.merge(grammar1, grammar2)
        actualGrammar = merger.merge(actualGrammar, grammar3)
        assertEquals(expectedGrammar.rules, actualGrammar.rules)
    }

    @Test
    fun mergeThreeGrammarsOneHasSensitiveToken() {
        val grammar1 = solver.solve(fixtureExamples[0].split(delim), setOf<Int>())
        val grammar2 = solver.solve(fixtureExamples[1].split(delim), setOf<Int>(3))
        val grammar3 = solver.solve(fixtureExamples[2].split(delim), setOf<Int>())
        val rFactory = RuleFactory(11)
        val r10 = rFactory.getTerminalRule("Bond", false)
        val r9 = rFactory.getTerminalRule("James", false)
        val r8 = rFactory.getABRule(r9, r10)
        val r7 = rFactory.getTerminalRule("bond,", false)
        val r6 = rFactory.getProductionRule(
            setOf<Rule>(
                TerminalRule("$numStar.$numStar.$numStar.$numStar:54321", true),
                ABRule(7, 8)
            )
        )
        val r5 = rFactory.getTerminalRule("$alphaStar", false)
        val r4 = rFactory.getABRule(r5, r6)
        val r3 = rFactory.getTerminalRule("$alphaStar", false)
        val r2 = rFactory.getABRule(r3, r4)
        val r1 = rFactory.getTerminalRule("$alphaStar", false)
        val r0 = rFactory.getABRule(r1, r2)
        val rmap = MutableRulesMap()
        rmap[0] = r0
        rmap[1] = r1
        rmap[2] = r2
        rmap[3] = r3
        rmap[4] = r4
        rmap[5] = r5
        rmap[6] = r6
        rmap[7] = r7
        rmap[8] = r8
        rmap[9] = r9
        rmap[10] = r10
        val expectedGrammar = HeapCNFGrammar(rmap)
        var actualGrammar = merger.merge(grammar1, grammar2)
        actualGrammar = merger.merge(actualGrammar, grammar3)
        assertEquals(expectedGrammar.rules, actualGrammar.rules)
    }
}
