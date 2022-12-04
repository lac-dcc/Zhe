package zhe.ParSy.Grammar

import zhe.ParSy.Grammar.Rule.ProductionRule

public class HeapCNFGrammar : IGrammar {
    override val rules: RulesMap
    override val root: ProductionRule
        get() = this.rules[0]!!

    constructor(rules: RulesMap) {
        this.rules = rules
    }

    override fun toString(): String {
        return AntlrPrinter(this).toString()
    }

    companion object {
        fun maxSize(numTokens: Int): Int {
            return 2 * numTokens - 1
        }
    }
}
