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

    // compressRuleSet takes a set of rules as an input, and returns the
    // compressed version of these rules. By compressed we mean that we try to
    // merge the rules into regex expressions as much as possible (as long as
    // the merge result isn't the lattice top).
    fun compressRuleSet(rules: Set<Rule>): Set<Rule> {
	if (rules.size < 2) {
	    return rules
	}

	println("Compressing rules ${rules}")

	var compressedRules = setOf<TerminalRule>()
	var otherRules = mutableSetOf<Rule>()
	var prevRegex = ""
	rules.forEach { rule ->
	    println("In new iteration in outer forEach")

	    if (rule !is TerminalRule) {
		otherRules.plusAssign(rule)
		return@forEach
	    }

	    if (compressedRules.size == 0) {
		println("Compressed rules is empty")
		compressedRules = setOf<TerminalRule>(rule)
		return@forEach
	    }

	    var newCompressedRules = mutableSetOf<TerminalRule>()
	    var curRegex = rule.toString()
	    compressedRules.forEach { prevRule -> 
		println("In new iteration in inner forEach. Previous rule: ${prevRule.toString()}")
		prevRegex = prevRule.toString()
                val resultRuleRegex = regexLattice.transform(prevRegex, curRegex)
		if (resultRuleRegex == regexLattice.top.rule) {
		    println("Went to top!")
		    newCompressedRules.plusAssign(TerminalRule(prevRegex))
		    return@forEach
		} else {
		    curRegex = resultRuleRegex
		}
	    }
	    newCompressedRules.plusAssign(TerminalRule(curRegex))

	    compressedRules = newCompressedRules
	}

	println("Compressed rules: ${compressedRules}")
	println("Other rules: ${otherRules}")

	return compressedRules.union(otherRules)
    }

    override fun merge(grammar: IGrammar, other: IGrammar) : IGrammar {
        val newRules: MutableRulesMap = MutableRulesMap()

        val numRules: Int = max(grammar.rules.entries.size, other.rules.entries.size)

        for(i in 0 until numRules) {
            val g1: ProductionRule = other.rules.getOrDefault(i, ProductionRule(i))
            val g2: ProductionRule = grammar.rules.getOrDefault(i, ProductionRule(i))

            val allRules: Set<Rule> = g1.rules.union(g2.rules)
	    val compressedRules = compressRuleSet(allRules)

	    println("Compressed rules: ${compressedRules}")

            newRules.put(i, ProductionRule(i, compressedRules))
        }

        return HeapCNFGrammar(newRules)
    }
}
