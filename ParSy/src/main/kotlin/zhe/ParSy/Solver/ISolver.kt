package zhe.ParSy.Solver

import zhe.ParSy.Grammar.IGrammar
import java.util.Stack

interface ISolver {
    fun solve(tokens : Stack<String>): IGrammar
}
