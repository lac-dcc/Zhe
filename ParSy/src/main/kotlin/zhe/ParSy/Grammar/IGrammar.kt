package zhe.ParSy.Grammar

import zhe.ParSy.Grammar.Rules.ProductionRule
import zhe.ParSy.Grammar.Rules.IRule

interface IGrammar {
    val rules: Map<Int, ProductionRule>
    val root: IRule
}