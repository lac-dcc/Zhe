package zhe.ParSy.Solver

import zhe.ParSy.Factory.RuleFactory
import zhe.ParSy.Grammar.HeapCNFGrammar
import zhe.ParSy.Grammar.IGrammar
import zhe.ParSy.Grammar.MutableRulesMap
import zhe.ParSy.Grammar.Rule.ProductionRule

public class TrivialSolver() : ISolver {
    override fun solve(
        tokens: List<String>,
        sensitiveTokenIndexes: Set<Int>
    ): IGrammar {
        val grammarRules = MutableRulesMap()
        if (tokens.size == 0) {
            return HeapCNFGrammar(grammarRules)
        }

        val tokenStack = TokenStack(tokens, sensitiveTokenIndexes)
        val rFactory = RuleFactory(HeapCNFGrammar.maxSize(tokens.size))
        var connectionRule = addInitialRules(tokenStack, grammarRules, rFactory)

        while (!tokenStack.isEmpty()) {
            val newTerminalRule = addTerminalRule(tokenStack, grammarRules, rFactory)
            connectionRule = rFactory.getABRule(newTerminalRule, connectionRule)
            grammarRules.put(connectionRule.id, connectionRule)
        }

        return HeapCNFGrammar(grammarRules)
    }

    fun addInitialRules(
        tokenStack: TokenStack,
        grammarRules: MutableRulesMap,
        rFactory: RuleFactory
    ): ProductionRule {
        val terminalRule1 = addTerminalRule(tokenStack, grammarRules, rFactory)
        if (tokenStack.isEmpty()) {
            return terminalRule1
        }
        val terminalRule2 = addTerminalRule(tokenStack, grammarRules, rFactory)
        // Notice we use the order 'terminalRule2 terminalRule1'. This is
        // because we are going backwards (LIFO) order.
        val connectionRule = rFactory.getABRule(terminalRule2, terminalRule1)
        grammarRules.put(connectionRule.id, connectionRule)
        return connectionRule
    }

    fun addTerminalRule(
        tokenStack: TokenStack,
        grammarRules: MutableRulesMap,
        rFactory: RuleFactory
    ): ProductionRule {
        val (token, isSensitive) = tokenStack.pop()
        val terminalRule = rFactory.getTerminalRule(token, isSensitive)
        grammarRules.put(terminalRule.id, terminalRule)
        return terminalRule
    }
}
