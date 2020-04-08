package zhe.ParSy.Grammar.Rules

import java.util.Stack
import zhe.ParSy.Grammar.RulesMap

class ABRule(var lRuleId: Int, var rRuleId: Int) : AbstractRule() {

    override fun match (tkns: MutableList<String>
                       , ruleTable: RulesMap
                       ): Boolean {
        if(tkns.isEmpty())
            return false
        
        val headTkn: String = tkns[0]
        val aRuleResp: Boolean = ruleTable[this.lRuleId]!!.match(tkns, ruleTable)
        var bRuleResp: Boolean = false
        
        if(aRuleResp){
            bRuleResp = ruleTable[this.rRuleId]!!.match(tkns, ruleTable)
            if(!bRuleResp)
                tkns.add(0, headTkn)
        }
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