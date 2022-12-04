package zhe.ParSy.Grammar

import zhe.ParSy.Grammar.Rule.ProductionRule

interface IGrammar {
    val rules: Map<Int, ProductionRule>
    val root: ProductionRule
}
