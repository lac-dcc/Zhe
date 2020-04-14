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
        rules.forEach { _, rule ->
                output += rule.toString() + "\n"
        }
        return output
    }
}
