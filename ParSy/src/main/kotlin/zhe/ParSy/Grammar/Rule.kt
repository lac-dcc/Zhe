package zhe.ParSy.Grammar

import zhe.ParSy.Grammar.RulesMap

sealed class Rule() {
    class TerminalRule(var pattern:String?) : Rule() {

        override fun toString() : String {
            return "$pattern"
        }

        override fun equals(other:Any?) : Boolean {
            if(other !is TerminalRule) 
                return false
            return other.pattern == this.pattern
        }
    }

    class ABRule(var lRuleId: Int, var rRuleId: Int) : Rule() {

        override fun toString() : String {
            return "R${lRuleId} R${rRuleId}"
        }

        override fun equals(other:Any?) : Boolean {
            if(other !is ABRule) 
                return false
            
            return other.lRuleId == this.lRuleId && other.rRuleId == this.rRuleId
        }
    }

    class ProductionRule(val id: Int, val rules:Set<Rule>) : Rule() {

        constructor(rid: Int): this(rid, setOf<Rule>())

        constructor(rid: Int, abRule: ABRule): this(rid, setOf<Rule>(abRule))

        constructor(rid: Int, tRule: TerminalRule): this(rid, setOf<Rule>(tRule)) 

        override fun toString() : String {
            var output: String = "R${this.id} :: "
            this.rules.forEachIndexed({index: Int, rule: Rule ->
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

    override fun hashCode() : Int {
        return ("$this").hashCode()
    }
}