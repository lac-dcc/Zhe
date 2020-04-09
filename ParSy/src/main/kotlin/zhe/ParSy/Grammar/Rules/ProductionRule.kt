package zhe.ParSy.Grammar.Rules

import java.util.Stack
import zhe.ParSy.Grammar.RulesMap

class ProductionRule(val id: Int, val rules:Set<IRule>) : AbstractRule() {

    constructor(rid: Int): this(rid, setOf<IRule>())

    constructor(rid: Int, abRule: ABRule): this(rid, setOf<IRule>(abRule))

    constructor(rid: Int, tRule: TerminalRule): this(rid, setOf<IRule>(tRule)) 

    fun union(nid: Int, other: ProductionRule): ProductionRule {
        val nRules: Set<IRule> = this.rules.union(other.rules)
        return ProductionRule(nid, nRules)
    }

    override fun match (tkns: List<String>
                       , ruleTable: RulesMap
                       ): Boolean {

        val aux: Boolean = this.rules.any({rule: IRule -> 
            rule.match(tkns, ruleTable)
        })
        return aux
    }

    override fun toString() : String {
        var output: String = "R${this.id} :: "
        this.rules.forEachIndexed({index: Int, rule:IRule ->
            if(index > 0)
                output += " | "
            output += rule.toString()
        })
        return output
    }

    override fun equals(other: Any?): Boolean {
        if(other !is ProductionRule)
            return false

        val nOther: ProductionRule = other
        return this.id == nOther.id 
               && this.rules.size == nOther.rules.size 
               && this.rules.union(nOther.rules).size == this.rules.size
    }
}