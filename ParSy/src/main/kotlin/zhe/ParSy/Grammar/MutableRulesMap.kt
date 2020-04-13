package zhe.ParSy.Grammar

import zhe.ParSy.Grammar.Rule.ProductionRule

typealias MutableRulesMap = MutableMap<Int, ProductionRule>
fun MutableRulesMap() : MutableRulesMap = mutableMapOf<Int, ProductionRule>()

