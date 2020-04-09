package zhe.ParSy.Grammar.Rules

import java.util.Stack
import zhe.ParSy.Grammar.RulesMap


class TerminalRule(var pattern:String?) : AbstractRule() {

    override fun match (tkns: List<String>
                       , ruleTable: RulesMap
                       ): Boolean { 

        return !tkns.isEmpty() && tkns.first() == pattern
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
