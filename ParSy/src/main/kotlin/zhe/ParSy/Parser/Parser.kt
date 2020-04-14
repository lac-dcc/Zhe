package zhe.ParSy.Parser

import zhe.ParSy.Grammar.IGrammar
import zhe.ParSy.Grammar.Rule
import zhe.ParSy.Grammar.Rule.TerminalRule
import zhe.ParSy.Grammar.Rule.ABRule
import zhe.ParSy.Grammar.Rule.ProductionRule

import java.util.Stack

class Parser : IParser {
    override fun parse(tkns: List<String>, grammar: IGrammar): Boolean {
        return this.parse(grammar.root, tkns, grammar)
    }

    private fun parse(node: Rule, tkns: List<String>, grammar: IGrammar) : Boolean {
        return !tkns.isEmpty() && 
        when(node){
            is TerminalRule -> tkns.first() == node.pattern
            is ABRule -> parse(node.lRule(grammar.rules), tkns.drop(0), grammar) &&
                         parse(node.rRule(grammar.rules), tkns.drop(1), grammar)
            is ProductionRule -> node.rules.any({rule: Rule -> parse(rule, tkns, grammar)})
        }

    }
}