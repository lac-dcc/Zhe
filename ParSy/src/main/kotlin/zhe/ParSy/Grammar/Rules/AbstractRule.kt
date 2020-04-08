package zhe.ParSy.Grammar.Rules

import zhe.ParSy.Grammar.RulesMap

abstract class AbstractRule : IRule {

    override abstract fun match (tkns: MutableList<String>
                                , ruleTable: RulesMap
                                ): Boolean

    override fun hashCode() : Int {
        return ("$this").hashCode()
    }
}