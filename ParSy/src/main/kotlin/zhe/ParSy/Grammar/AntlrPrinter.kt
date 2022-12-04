package zhe.ParSy.Grammar

import zhe.ParSy.Grammar.Rule.ABRule
import zhe.ParSy.Grammar.Rule.TerminalRule
import zhe.ParSy.Regex.Pattern

// AntlrPrinter is a grammar printer that is capable of printing a given grammar
// in the Antlr syntax.
public class AntlrPrinter(val grammar: IGrammar) {
    override fun toString(): String {
        var grammarStr = ""

        grammarStr += "grammar ZheGrammar;\n"
        grammarStr += """
@members {
	Map<String, Boolean> isSensitive = newHashMap<String, Boolean>();
}
"""
        val sortedRules = grammar.rules.toSortedMap()
        var terminalRules = mutableMapOf<Int, TerminalRule>()
        var terminalRuleId = 0
        var newTerminalRuleIds = listOf<Int>()
        sortedRules.forEach { _, productionRule ->
            grammarStr += "r${productionRule.id}: "
            productionRule.rules.forEach { subRule ->
                if (subRule is ABRule) {
                    grammarStr += "r${subRule.lRuleId} r${subRule.rRuleId} "
                } else if (subRule is TerminalRule) {
                    grammarStr += "TOKEN$terminalRuleId "
                    terminalRules[terminalRuleId] = subRule
                    newTerminalRuleIds += terminalRuleId
                    terminalRuleId++
                }
            }

            if (newTerminalRuleIds.size > 0) {
                grammarStr += "\n"
                grammarStr += "{\n"
                // Add Antlr actions to set token as sensitive / not-sensitive.
                newTerminalRuleIds.forEach { ruleId ->
                    grammarStr += "\tisSensitive.put(\$TOKEN$ruleId.type, " +
                        "${terminalRules[ruleId]!!.isSensitive});\n"
                }
                grammarStr += "}"
                // Reset list of terminal rules
                newTerminalRuleIds = listOf<Int>()
            }
            grammarStr += ";\n"
        }

        // Add one rule per terminal token
        grammarStr += "\n"
        terminalRules.forEach { id, rule ->
            grammarStr += "TOKEN$id: ${Pattern.tokenize(rule.pattern)};\n"
        }

        // Add skip rules
        grammarStr += "\n"
        grammarStr += "WHITESPACE: ' ' -> skip;\n"

        return grammarStr
    }
}
