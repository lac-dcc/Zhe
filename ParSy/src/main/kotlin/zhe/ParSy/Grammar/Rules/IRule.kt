package zhe.ParSy.Grammar.Rules

import java.util.Stack
import zhe.ParSy.Grammar.RulesMap

interface IRule {
    fun match (tkns: List<String>
              , ruleTable: RulesMap
              ): Boolean
    override fun hashCode() : Int
    override fun equals(other: Any?): Boolean

}