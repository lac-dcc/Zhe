package zhe.ParSy

import zhe.ParSy.Grammar.IGrammar
import zhe.ParSy.Merger.HeapCNFMerger
import zhe.ParSy.Merger.IMerger
import zhe.ParSy.SensitiveMarker.SensitiveMarker
import zhe.ParSy.Solver.ISolver
import zhe.ParSy.Solver.TrivialSolver
import java.io.File

fun main(args: Array<String>) {
    if (args.size < 1) {
        println(
            "Unable to execute Zhe parser without arguments.\n" +
                "Usage: <program> <input-file>"
        )
        return
    }
    val inputFile = args[0]
    val tokenDelimeter = " "

    val solver: ISolver = TrivialSolver()
    val merger: IMerger = HeapCNFMerger()
    val sensitiveMarker: SensitiveMarker = SensitiveMarker(tokenDelimeter)

    // Parse each line (example) of the input file. For each new example, we
    // merge the grammar generated by it with the existing grammar. In the end,
    // we will have aggregated all examples into a single grammar, so we print
    // it.
    var tokens = listOf<String>()
    var g1: IGrammar = solver.solve(tokens, setOf<Int>())
    var numLines = 0
    File(inputFile).forEachLine { line: String ->
        numLines++

        val (sensitiveTokenIndexesList, cleanLine) =
            sensitiveMarker.findSensitiveTokens(line)

        val sensitiveTokenIndexes = sensitiveTokenIndexesList?.toSet()
            ?: setOf<Int>()

        tokens = cleanLine.split(tokenDelimeter)
        val g2 = solver.solve(tokens, sensitiveTokenIndexes)
        g1 = merger.merge(g1, g2)
    }
    println(g1)

    // val numSentencesInGrammar: Double = NumberOfSentencesInGrammar(g1).count()
    // print("$numLines $numSentencesInGrammar ${numLines / numSentencesInGrammar}")
}
