package zhe.ParSy.Grammar.Rules

import java.util.Stack
import zhe.ParSy.Grammar.RulesMap

interface IRule {
    fun match (tkns: MutableList<String>
              , ruleTable: RulesMap
              ): Boolean
}