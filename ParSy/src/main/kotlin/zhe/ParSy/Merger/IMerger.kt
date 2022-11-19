package zhe.ParSy.Merger

import zhe.ParSy.Grammar.IGrammar

interface IMerger {
    fun merge(grammar: IGrammar, other: IGrammar): IGrammar
}
