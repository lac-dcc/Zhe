package zhe.ParSy.Solver

import zhe.ParSy.Grammar.Rule.ProductionRule
import zhe.ParSy.Factory.RuleFactory
import zhe.ParSy.Grammar.IGrammar
import zhe.ParSy.Grammar.HeapCNFGrammar
import zhe.ParSy.Grammar.MutableRulesMap

public class TrivialSolver(): ISolver {
    override fun solve(
	tokens : List<String>,
	sensitiveTokenIndexes: Set<Int>
    ): IGrammar {
        val grammarRules: MutableRulesMap = MutableRulesMap()
	if (tokens.size == 0) {
	    return HeapCNFGrammar(grammarRules)
	}

        val rFactory: RuleFactory = RuleFactory(2 * tokens.size - 1)

        var connectionRule : ProductionRule? = null

	var i = tokens.size - 1
	val isRule1Sensitive = sensitiveTokenIndexes.contains(i)
        var rule1: ProductionRule = rFactory.getTerminalRule(tokens[i],
							     isRule1Sensitive)
	i--
        grammarRules.put(rule1.id, rule1)

        while (i >= 0) {
	    val token = tokens[i]
	    val isSensitiveToken = sensitiveTokenIndexes.contains(i)
            if (connectionRule == null) {
                val rule2  = rFactory.getTerminalRule(token, isSensitiveToken)
                grammarRules.put(rule2.id, rule2)

                connectionRule = rFactory.getABRule(
		    rule1.id, rule2.id, isRule1Sensitive, isSensitiveToken)
            } else {
                rule1 = rFactory.getTerminalRule(token, isSensitiveToken)
                grammarRules.put(rule1.id, rule1)

                connectionRule = rFactory.getABRule(
		    rule1.id, connectionRule.id, isRule1Sensitive, isSensitiveToken)
            }
            grammarRules.put(connectionRule.id, connectionRule)
	    i--
        }

        return HeapCNFGrammar(grammarRules)
    }
}
