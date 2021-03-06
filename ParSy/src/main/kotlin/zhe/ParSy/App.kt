package zhe.ParSy

import zhe.ParSy.Grammar.HeapCNFGrammar
import zhe.ParSy.Grammar.IGrammar

import zhe.ParSy.Solver.TrivialSolver
import zhe.ParSy.Solver.ISolver

import zhe.ParSy.Parser.Parser
import zhe.ParSy.Parser.IParser

import zhe.ParSy.Merger.IMerger
import zhe.ParSy.Merger.HeapCNFMerger

import zhe.ParSy.Metrics.NumberOfSentencesInGrammar

import java.util.Stack


fun main(args:Array<String>){

    val solver: ISolver = TrivialSolver()
    val merger: IMerger = HeapCNFMerger()
    val parser: IParser = Parser()

    val tokens: Stack<String> = Stack<String>()
    
    var index: Int = 0
    
    while(args[index].length <= 0)
        index += 1
        
    val arguments = args.sliceArray(index .. args.size-1)

    tokens.addAll(arguments[0].split(" "))

    var g1: IGrammar = solver.solve(tokens)
    for(i in 1 until arguments.size){
        if(arguments[i].length > 0){
            tokens.addAll(arguments[i].split(" "))
            
            val g2: IGrammar = solver.solve(tokens)

            g1 = merger.merge(g1, g2)
        }
    }
    println(g1)

    
    val numSentencesInGrammar: Double = NumberOfSentencesInGrammar(g1).count()
    print("${arguments.size} $numSentencesInGrammar ${arguments.size/numSentencesInGrammar}")
}