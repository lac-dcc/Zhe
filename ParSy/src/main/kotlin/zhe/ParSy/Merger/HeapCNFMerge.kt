package zhe.ParSy.Merger

import zhe.ParSy.Grammar.MutableRulesMap
import zhe.ParSy.Grammar.Rule.ProductionRule
import zhe.ParSy.Grammar.Rule.TerminalRule
import zhe.ParSy.Grammar.HeapCNFGrammar
import zhe.ParSy.Grammar.IGrammar
import zhe.ParSy.Grammar.Rule
import zhe.ParSy.Regex.Lattice

import kotlin.math.max

public class HeapCNFMerger : IMerger {

    companion object {
	private val regexLattice = Lattice()
    }

    private fun compressRuleSet(rules: Set<Rule>): Set<Rule> {
	if (rules.size < 2) {
	    return rules
	}

	println("Compressing rules ${rules}")

	var compressedRules = mutableSetOf<Rule>()
	var prevRegex = ""
	rules.forEach { rule ->
	    if (rule is TerminalRule) {
		val resultRuleRegex = regexLattice.transform(prevRegex,
							     rule.toString())
		if (resultRuleRegex == ".*") {
		    println("Got to top! prevRegex: $prevRegex")
		    println("Got to top! rule.toString(): ${rule.toString()}")
		    compressedRules.plusAssign(TerminalRule(prevRegex))
		    prevRegex = rule.toString()
		} else {
		    println("Did not get to top! resultRuleRegex: $resultRuleRegex")
		    prevRegex = resultRuleRegex
		}
	    } else {
		compressedRules.plusAssign(rule)
	    }
	}
	if (prevRegex != "") {
	    compressedRules.plusAssign(TerminalRule(prevRegex))
	}

	println("Compressed rules: ${compressedRules}")

	return compressedRules
    }

    override fun merge(grammar: IGrammar, other: IGrammar) : IGrammar {
        val newRules: MutableRulesMap = MutableRulesMap()

        val numRules: Int = max(grammar.rules.entries.size, other.rules.entries.size)

        for(i in 0 until numRules) {
            val g1: ProductionRule = other.rules.getOrDefault(i, ProductionRule(i))
            val g2: ProductionRule = grammar.rules.getOrDefault(i, ProductionRule(i))

            val newRuleSet: Set<Rule> = g1.rules.union(g2.rules)
	    val compressedRuleSet = compressRuleSet(newRuleSet)

            newRules.put(i, ProductionRule(i, compressedRuleSet))
        }

        return HeapCNFGrammar(newRules)
    }
}
