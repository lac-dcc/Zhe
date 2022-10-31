package zhe.ParSy.Grammar

import zhe.ParSy.Grammar.RulesMap

sealed class Rule() {
    class TerminalRule(var pattern: String?, val isSensitive: Boolean) : Rule() {
	constructor(pattern: String?): this(pattern, false)

        override fun toString() : String {
            return "$pattern"
        }

        override fun equals(other:Any?) : Boolean {
            if(other !is TerminalRule) 
                return false
            return other.pattern == this.pattern
        }
    }

    class ABRule(
	private val lRuleId: Int,
	private val rRuleId: Int,
	val isLeftSensitive: Boolean,
	val isRightSensitive: Boolean
    ) : Rule() {

        fun lRule(table: RulesMap) : Rule {
            return table[this.lRuleId]!!
        }

        fun rRule(table: RulesMap) : Rule {
            return table[this.rRuleId]!!
        }

        override fun toString() : String {
	    val leftPrefix = if (!isLeftSensitive) "R" else "S"
	    val rightPrefix = if (!isRightSensitive) "R" else "S"
            return "${leftPrefix}${lRuleId} ${rightPrefix}${rRuleId}"
        }

        override fun equals(other:Any?) : Boolean {
            if(other !is ABRule) 
                return false
            
            return other.lRuleId == this.lRuleId && other.rRuleId == this.rRuleId
        }
    }

    class ProductionRule(
	val id: Int,
	val rules: Set<Rule>,
	val isSensitive: Boolean
    ) : Rule() {

        constructor(rid: Int): this(rid, setOf<Rule>(), false)

        constructor(rid: Int, abRule: ABRule): this(
	    rid, setOf<Rule>(abRule),
	    abRule.isLeftSensitive && abRule.isRightSensitive)

        constructor(rid: Int, tRule: TerminalRule): this(rid, setOf<Rule>(tRule), tRule.isSensitive)

        constructor(rid: Int, tRule: TerminalRule, isSensitive: Boolean):
	    this(rid, setOf<Rule>(tRule), isSensitive) 

        override fun toString() : String {
	    val prefix = if (!isSensitive) "R" else "S"
            var output: String = "${prefix}${this.id} :: "
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

    override abstract fun toString() : String
    override abstract fun equals(other:Any?) : Boolean
    
    override fun hashCode() : Int {
        return ("$this").hashCode()
    }
}
