package zhe.ParSy.Parser

import zhe.ParSy.Grammar.IGrammar

interface IParser {
    fun parse(tkns: List<String>, grammar: IGrammar): Boolean
}