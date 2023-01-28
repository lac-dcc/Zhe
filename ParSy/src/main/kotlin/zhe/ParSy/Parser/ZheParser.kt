package zhe.ParSy.Parser

import zhe.ParSy.Grammar.HeapCNFGrammar
import zhe.ParSy.Grammar.IGrammar
import zhe.ParSy.Grammar.RulesMap
import zhe.ParSy.Merger.HeapCNFMerger
import zhe.ParSy.Merger.IMerger
import zhe.ParSy.SensitiveMarker.SensitiveMarker
import zhe.ParSy.Solver.ISolver
import zhe.ParSy.Solver.TrivialSolver
import java.io.File

// ZheParser can be used by other components to update a grammar file
// online. That is, update the grammar file as new log examples show up.
class ZheParser(
    private val tokenDelimeter: String,
    private val grammarFilePath: String
) {
    private var grammar: IGrammar = HeapCNFGrammar(RulesMap())
    private val solver: ISolver = TrivialSolver()
    private val merger: IMerger = HeapCNFMerger()
    private val sensitiveMarker: SensitiveMarker = SensitiveMarker(tokenDelimeter)

    fun update(line: String): Boolean {
        val (sensitiveTokenIndexesList, cleanLine) =
            sensitiveMarker.findSensitiveTokens(line)
        val sensitiveTokenIndexes = sensitiveTokenIndexesList?.toSet()
            ?: setOf<Int>()
        val tokens = cleanLine.split(tokenDelimeter)
        val newGrammar = solver.solve(tokens, sensitiveTokenIndexes)
        grammar = merger.merge(grammar, newGrammar)
        val grammarWasUpdated = true
        if (grammarWasUpdated) {
            File(grammarFilePath).writeText(grammar.toString())
        }
        return grammarWasUpdated
    }
}
