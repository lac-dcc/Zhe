package zhe.ParSy.Metrics

import zhe.ParSy.Grammar.IGrammar
import zhe.ParSy.Grammar.Rule
import zhe.ParSy.Grammar.Rule.TerminalRule
import zhe.ParSy.Grammar.Rule.ABRule
import zhe.ParSy.Grammar.Rule.ProductionRule


class NumberOfSentencesInGrammar(val grammar: IGrammar){

    fun count(): Double = count(grammar.root)

    fun count(rule: Rule): Double {
        return when(rule){
            is TerminalRule -> 1.0
            is ABRule -> count(rule.lRule(grammar.rules)) * count(rule.rRule(grammar.rules))
            is ProductionRule -> rule.rules.map { r -> count(r) }.reduce { agg, v -> agg + v}
        }
    }
}