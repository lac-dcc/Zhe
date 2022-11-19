package zhe.ParSy.Solver

import zhe.ParSy.Grammar.IGrammar

interface ISolver {
    fun solve(tokens: List<String>, sensitiveTokenIndexes: Set<Int>): IGrammar
}
