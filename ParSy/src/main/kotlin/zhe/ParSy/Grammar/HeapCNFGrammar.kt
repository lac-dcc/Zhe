package zhe.ParSy.Grammar

import zhe.ParSy.Grammar.Rules.IRule

public class HeapCNFGrammar : IGrammar{
    override val rules: RulesMap
    override val root: IRule
        get() = this.rules[0]!!

    constructor(rules: RulesMap) {
        this.rules = rules
    }

    override fun toString() : String {
        var output: String = ""
        rules.forEach { 
            _, rule ->
                output += rule.toString() + "\n"
        }
        return output
    }
}
