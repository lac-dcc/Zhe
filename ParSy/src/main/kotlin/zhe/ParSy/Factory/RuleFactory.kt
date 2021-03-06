package zhe.ParSy.Factory

import zhe.ParSy.Grammar.Rule.ProductionRule
import zhe.ParSy.Grammar.Rule.ABRule
import zhe.ParSy.Grammar.Rule.TerminalRule

class RuleFactory(val maxRules: Int) {
    var index: Int
    init{
        index = maxRules
    }

    fun getNextId():Int{
        index -= 1
        return index
    }

    fun getABRule(lRule: Int, rRule: Int) : ProductionRule {
        val rid:Int = this.getNextId()
        return ProductionRule(rid, ABRule(lRule, rRule))
    }

    fun getTerminalRule(tkn:String) : ProductionRule { 
        val rid = this.getNextId()
        return ProductionRule(rid, TerminalRule(tkn))
    }
}