package zhe.ParSy.SensitiveMarker

import java.io.File
import kotlin.text.Regex

val sensitiveRegex = Regex("<(\\p{Alnum}+)>(.+)</\\1>")
val tokenDelimeter = " "

// TODO: wrap this in a class and refactor -aholmquist 2022-10-30

fun findSensitiveWords(ln: String): Pair<List<Int>?, String> {
    println("In findSensitiveWords. Received line: ${ln}")

    var matches = sensitiveRegex.findAll(ln)
    if (matches.count() < 1) {
	return Pair(null, ln)
    }

    // Keep a map of <token starting position> -> <token index in given line>
    var tokenPositionToIndex = mutableMapOf<Int, Int>()
    var tokenPositionToIndexPointer = 0
    var indexInLine = 0
    val splitLine = ln.split(tokenDelimeter)
    splitLine.forEach { token ->
        tokenPositionToIndex[tokenPositionToIndexPointer] = indexInLine
        indexInLine++
        tokenPositionToIndexPointer += token.length + tokenDelimeter.length
    }
    val lastWordIndex = indexInLine - 1

    var matchIndexes = mutableListOf<Int>()
    var nonMarkedLine = ""
    var nonMarkedLineIdx = 0
    matches.iterator().forEach { match ->
        val start = match.range.start
	val end = match.range.endInclusive

	// Add the index of each token in the matched sensitive string
	var matchedWordIndexInLine = tokenPositionToIndex.getValue(start)
	val rightLimit = (
	    if (end+tokenDelimeter.length >= ln.length) lastWordIndex
            else tokenPositionToIndex.getValue(end+tokenDelimeter.length)
	)
	while (matchedWordIndexInLine <= rightLimit) {
            matchIndexes += matchedWordIndexInLine
	    matchedWordIndexInLine++
	}

	// Add pieces of line without the markdown characters, so that original
	// (non-marked) input can be easily processed downstream.
	//
	// Before
	nonMarkedLine += ln.substring(nonMarkedLineIdx, start)
	// Middle
	val (_, middleValue) = match.destructured
	nonMarkedLine += middleValue
	// Update index
	nonMarkedLineIdx = end + 1
	println("Non marked line: ${nonMarkedLine}")
    }
    // Add rest of the line
    nonMarkedLine += ln.substring(nonMarkedLineIdx)

    return Pair(matchIndexes, nonMarkedLine)
}
