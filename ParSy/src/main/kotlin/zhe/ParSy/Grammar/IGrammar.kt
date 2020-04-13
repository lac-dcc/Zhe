package zhe.ParSy.Grammar

import zhe.ParSy.Grammar.Rule.ProductionRule
import zhe.ParSy.Grammar.Rule

interface IGrammar {
    val rules: Map<Int, ProductionRule>
    val root: Rule
}