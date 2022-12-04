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

    @Test
    fun grammarToStringHelloWorld() {
        val tokens = "Hello World".split(" ")
        val sensitiveTokenIndexes = setOf<Int>()
        val solver = TrivialSolver()
        val grammar = solver.solve(tokens, sensitiveTokenIndexes)
        assertEquals(
            """grammar ZheGrammar;

@members {
	Map<String, Boolean> isSensitive = newHashMap<String, Boolean>();
}
r0: r1 r2 ;
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

WHITESPACE: ' ' -> skip;
""",
            grammar.toString()
        )
    }
}
