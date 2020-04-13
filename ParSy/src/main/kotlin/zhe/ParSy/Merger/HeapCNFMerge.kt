package zhe.ParSy.Merger

import zhe.ParSy.Grammar.MutableRulesMap
import zhe.ParSy.Grammar.Rule.ProductionRule
import zhe.ParSy.Grammar.HeapCNFGrammar
import zhe.ParSy.Grammar.IGrammar
import zhe.ParSy.Grammar.Rule

import kotlin.math.max

public class HeapCNFMerger : IMerger {

    override fun merge(grammar: IGrammar, other: IGrammar) : IGrammar {
        val newRules: MutableRulesMap = MutableRulesMap()

        val numRules: Int = max(grammar.rules.entries.size, other.rules.entries.size)

        for(i in 0 until numRules) {
            val g1: ProductionRule = other.rules.getOrDefault(i, ProductionRule(i))
            val g2: ProductionRule = grammar.rules.getOrDefault(i, ProductionRule(i))

            val nRules: Set<Rule> = g1.rules.union(g2.rules)
            newRules.put(i, ProductionRule(i, nRules))
        }

        return HeapCNFGrammar(newRules)
    }
}