package zhe.ParSy.Grammar.Rules

import java.util.Stack
import zhe.ParSy.Grammar.RulesMap


class TerminalRule(var pattern:String?) : AbstractRule() {

    override fun match (tkns: MutableList<String>
                       , ruleTable: RulesMap
                       ): Boolean {

        if(tkns.isEmpty())
            return false

        val tkn: String = tkns[0]
        
        if(tkn == pattern)
            tkns.removeAt(0)
        
        return tkn == pattern
    }

    override fun toString() : String {
        return "$pattern"
    }

    override fun equals(other:Any?) : Boolean {
        if(other !is TerminalRule) 
            return false
        return other.pattern == this.pattern
    }
}