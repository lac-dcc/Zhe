package zhe.ParSy.Grammar.Rules

import java.util.Stack
import zhe.ParSy.Grammar.RulesMap

class ABRule(var lRuleId: Int, var rRuleId: Int) : AbstractRule() {

    override fun match (tkns: List<String>
                       , ruleTable: RulesMap
                       ): Boolean {
        if(tkns.isEmpty())
            return false
        
        val aRuleResp: Boolean = ruleTable[this.lRuleId]!!.match(tkns, ruleTable)
        val bRuleResp: Boolean = ruleTable[this.rRuleId]!!.match(tkns.drop(1), ruleTable)
        
        return aRuleResp && bRuleResp
    } 
    
    override fun toString() : String {
        return "R${lRuleId} R${rRuleId}"
    }

    override fun equals(other:Any?) : Boolean {
        if(other !is ABRule) 
            return false
        
        return other.lRuleId == this.lRuleId && other.rRuleId == this.rRuleId
    }
}