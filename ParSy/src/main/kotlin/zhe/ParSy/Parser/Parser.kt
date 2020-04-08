package zhe.ParSy.Parser

import zhe.ParSy.Grammar.IGrammar

import java.util.Stack

class Parser : IParser {
    override fun parse(tkns: MutableList<String>, grammar: IGrammar): Boolean {
        return grammar.root.match(tkns, grammar.rules)
    }
}