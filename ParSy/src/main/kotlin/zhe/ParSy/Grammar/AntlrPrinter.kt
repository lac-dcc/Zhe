package zhe.ParSy.Grammar

import zhe.ParSy.Grammar.Rule.ABRule
import zhe.ParSy.Grammar.Rule.TerminalRule
import zhe.ParSy.Regex.Pattern

// AntlrPrinter is a grammar printer that is capable of printing a given grammar
// in the Antlr syntax.
public class AntlrPrinter(val grammar: IGrammar) {
    private val sectionPrefix = "\n"

    fun string(): String {
        var grammarStr = ""
        grammarStr += header()
        val (bodyStr, terminalRules) = body()
        grammarStr += bodyStr
        grammarStr += footer(terminalRules)
        return grammarStr
    }

    fun header(): String {
        return """grammar ZheGrammar;

@header {
	import java.util.Map;
	import java.util.HashMap;
}

@members {
	Map<Integer, Boolean> isSensitive = new HashMap<Integer, Boolean>();
}
"""
    }

    fun body(): Pair<String, Map<Int, TerminalRule>> {
        var s = sectionPrefix
        val sortedRules = grammar.rules.toSortedMap()
        var terminalRules = mutableMapOf<Int, TerminalRule>()
        var terminalRuleId = 0
        var newTerminalRuleIds = listOf<Int>()
        sortedRules.forEach { _, productionRule ->
            var subRules = productionRule.rules
            s += "r${productionRule.id}:\n\t"

            // If applicable, we need to first include the AB rule
            val abRule = subRules.find { it is ABRule } as ABRule?
            if (abRule != null) {
                subRules -= abRule
                s += "(r${abRule.lRuleId} r${abRule.rRuleId})"
            }

            subRules.forEach { subRule ->
                if (productionRule.rules.size > 1) {
                    s += "\n\t| "
                }
                if (subRule is TerminalRule) {
                    s += "TOKEN$terminalRuleId"
                    s += " {"
                    // Add Antlr actions to set token as sensitive / not-sensitive.
                    s += " isSensitive.put(\$TOKEN$terminalRuleId.type, " +
                        "${subRule.isSensitive}); "
                    s += "}"
                    terminalRules[terminalRuleId] = subRule
                    terminalRuleId++
                } else {
                    throw Exception("Expected rule to be TerminalRule")
                }
            }

            if (s[s.length - 1] == '|') {
                // Remove trailing | and whitespace
                s = s.trimEnd { it == ' ' || it == '|' }
            }

            s += "\n\t;\n"
        }
        return Pair(s, terminalRules)
    }

    fun footer(terminalRules: Map<Int, TerminalRule>): String {
        return terminalTokenRules(terminalRules) +
            skipRules()
    }

    fun terminalTokenRules(terminalRules: Map<Int, TerminalRule>): String {
        // Add one rule per terminal token
        var s = sectionPrefix
        terminalRules.forEach { id, rule ->
            s += "TOKEN$id: ${Pattern.tokenize(rule.pattern)};\n"
        }
        return s
    }

    fun skipRules(): String {
        var s = sectionPrefix
        s += "WHITESPACE: ' ' -> skip;\n"
        return s
    }
}
