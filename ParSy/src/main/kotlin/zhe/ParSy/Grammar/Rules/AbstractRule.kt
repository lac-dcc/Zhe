package zhe.ParSy.Grammar.Rules

import zhe.ParSy.Grammar.RulesMap

abstract class AbstractRule : IRule {

    override abstract fun match (tkns: List<String>
                                , ruleTable: RulesMap
                                ): Boolean

    override fun hashCode() : Int {
        return ("$this").hashCode()
    }
    override abstract fun equals(other: Any?): Boolean

}