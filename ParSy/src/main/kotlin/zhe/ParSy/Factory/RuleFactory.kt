package zhe.ParSy.Factory

import zhe.ParSy.Grammar.Rule
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

    fun getTerminalRule(tkn: String, isSensitive: Boolean) : ProductionRule { 
        val rid = this.getNextId()
        return ProductionRule(rid, TerminalRule(tkn, isSensitive), isSensitive)
    }

    fun getABRule(
	lRule: Int,
	rRule: Int,
	isLeftSensitive: Boolean,
	isRightSensitive: Boolean,
    ) : ProductionRule {
        val rid: Int = this.getNextId()
        return ProductionRule(rid, ABRule(lRule, rRule, isLeftSensitive,
					  isRightSensitive))
    }

    fun getABRule(lRule: ProductionRule, rRule: ProductionRule): ProductionRule {
	return getABRule(
	    lRule.id,
	    rRule.id,
	    lRule.isSensitive,
	    rRule.isSensitive,
	)
    }
}
