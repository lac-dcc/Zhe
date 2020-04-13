package zhe.ParSy.Solver

import zhe.ParSy.Grammar.Rule.ProductionRule
import zhe.ParSy.Factory.RuleFactory
import zhe.ParSy.Grammar.IGrammar
import zhe.ParSy.Grammar.HeapCNFGrammar
import zhe.ParSy.Grammar.MutableRulesMap 

import java.util.Stack

public class TrivialSolver(): ISolver {
    override fun solve(tokens : Stack<String>): IGrammar {
        val grammarRules: MutableRulesMap = MutableRulesMap()
        val rFactory: RuleFactory = RuleFactory(2 * tokens.size - 1)

        var connectionRule : ProductionRule? = null

        var rule: ProductionRule = rFactory.getTerminalRule(tokens.pop())
        grammarRules.put(rule.id, rule)

        while(!tokens.isEmpty()){
            if(connectionRule == null){
                val rule2: ProductionRule = rFactory.getTerminalRule(tokens.pop())
                grammarRules.put(rule2.id, rule2)

                connectionRule = rFactory.getABRule(rule2.id, rule.id)
            }else{
                rule = rFactory.getTerminalRule(tokens.pop())
                grammarRules.put(rule.id, rule)

                connectionRule = rFactory.getABRule(rule.id, connectionRule.id)
            }
            grammarRules.put(connectionRule.id, connectionRule)
        }

        return HeapCNFGrammar(grammarRules)
    }
}
