package zhe.ParSy.Grammar

import zhe.ParSy.Grammar.Rule

public class HeapCNFGrammar : IGrammar{
    override val rules: RulesMap
    override val root: Rule
        get() = this.rules[0]!!

    constructor(rules: RulesMap) {
        this.rules = rules
    }

    override fun toString() : String {
        var output: String = ""
	// Sort first for consistency when printing map.
	val sortedRules = rules.toSortedMap()
        sortedRules.forEach { _, rule ->
                output += rule.toString() + "\n"
        }
        return output
    }

    companion object {
	fun maxSize(numTokens: Int): Int {
	    return 2 * numTokens - 1
	}
    }
}
