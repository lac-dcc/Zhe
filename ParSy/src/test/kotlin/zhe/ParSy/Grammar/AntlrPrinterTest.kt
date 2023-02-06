package zhe.ParSy.Grammar

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import zhe.ParSy.Merger.HeapCNFMerger
import zhe.ParSy.Solver.TrivialSolver

public class AntlrPrinterTest {
    companion object {
        private val merger = HeapCNFMerger()
        private val solver = TrivialSolver()
    }

    @Test
    fun printHelloWorld() {
        val tokens = "Hello World".split(" ")
        val sensitiveTokenIndexes = setOf<Int>()
        val grammar = solver.solve(tokens, sensitiveTokenIndexes)
        val printer = AntlrPrinter(grammar)
        assertEquals(
            printer.header() + """
r0: (r1 r2);
r1: TOKEN0
{
	isSensitive.put(${"$"}TOKEN0.type, false);
};
r2: TOKEN1
{
	isSensitive.put(${"$"}TOKEN1.type, false);
};

TOKEN0: [H][e][l][l][o];
TOKEN1: [W][o][r][l][d];
""" + printer.skipRules(),
            printer.string()
        )
    }

    @Test
    fun printGrammarMergedWithRegex() {
        val tokens1 = "received host 127.0.0.1".split(" ")
        val tokens2 = "received host 8.8.8.8".split(" ")
        val sensitiveTokenIndexes = setOf<Int>()
        val grammar1 = solver.solve(tokens1, sensitiveTokenIndexes)
        val grammar2 = solver.solve(tokens2, sensitiveTokenIndexes)
        val grammar = merger.merge(grammar1, grammar2)
        val printer = AntlrPrinter(grammar)
        assertEquals(
            printer.header() + """
r0: (r1 r2);
r1: TOKEN0
{
	isSensitive.put(${"$"}TOKEN0.type, false);
};
r2: (r3 r4);
r3: TOKEN1
{
	isSensitive.put(${"$"}TOKEN1.type, false);
};
r4: TOKEN2
{
	isSensitive.put(${"$"}TOKEN2.type, false);
};

TOKEN0: [r][e][c][e][i][v][e][d];
TOKEN1: [h][o][s][t];
TOKEN2: [0-9]*[.][0-9]*[.][0-9]*[.][0-9]*;
""" + printer.skipRules(),
            printer.string()
        )
    }
}
