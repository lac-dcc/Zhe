package zhe.ParSy.Grammar

sealed class Rule() {

    // Only terminal rules can be sensitive. That is because if we were to
    // consider an entire rule to be sensitive, then every token that appeared
    // in that position in the example text given by the user would also become
    // sensitive.
    class TerminalRule(var pattern: String, val isSensitive: Boolean) : Rule() {
        constructor(pattern: String) : this(pattern, false)

        override fun toString(): String {
            if (isSensitive) {
                return "<S>$pattern"
            } else {
                return "<N>$pattern"
            }
        }

        override fun equals(other: Any?): Boolean {
            if (other !is TerminalRule) {
                return false
            }
            return other.pattern == this.pattern &&
                other.isSensitive == this.isSensitive
        }
    }

    class ABRule(private val lRuleId: Int, private val rRuleId: Int) : Rule() {

        fun lRule(table: RulesMap): Rule {
            return table[this.lRuleId]!!
        }

        fun rRule(table: RulesMap): Rule {
            return table[this.rRuleId]!!
        }

        override fun toString(): String {
            return "R$lRuleId R$rRuleId"
        }

        override fun equals(other: Any?): Boolean {
            if (other !is ABRule) {
                return false
            }

            return other.lRuleId == this.lRuleId && other.rRuleId == this.rRuleId
        }
    }

    class ProductionRule(val id: Int, val rules: Set<Rule>) : Rule() {

        constructor(rid: Int) : this(rid, setOf<Rule>())

        constructor(rid: Int, abRule: ABRule) : this(rid, setOf<Rule>(abRule))

        constructor(rid: Int, tRule: TerminalRule) : this(rid, setOf<Rule>(tRule))

        override fun toString(): String {
            var output: String = "R${this.id} :: "
            this.rules.forEachIndexed({ index: Int, rule: Rule ->
                if (index > 0) {
                    output += " | "
                }
                output += rule.toString()
            })
            return output
        }

        override fun equals(other: Any?): Boolean {
            if (other !is ProductionRule) {
                return false
            }

            val nOther: ProductionRule = other
            return this.id == nOther.id && this.rules.size == nOther.rules.size && this.rules.union(nOther.rules).size == this.rules.size
        }
    }

    abstract override fun toString(): String
    abstract override fun equals(other: Any?): Boolean

    override fun hashCode(): Int {
        return ("$this").hashCode()
    }
}
