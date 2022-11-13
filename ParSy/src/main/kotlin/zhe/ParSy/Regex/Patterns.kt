package zhe.ParSy.Regex

val alphaStar = "\\p{Alpha}*"
val numStar = "\\d*"
val alnumStar = "\\p{Alnum}*"
val punctStar = "\\p{Punct}*"
val dotStar = ".*"

val allAlphas: List<String> = listOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "k",
				   "l", "m", "n", "o", "p", "q", "r", "s", "t", "u",
				   "v", "w", "x", "y", "z",
				   "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
				   "K", "L", "M", "N", "O", "P", "R", "S", "T", "U",
				   "V", "W", "X", "Y", "Z")
val allNums: List<String> = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
val allAlnums: List<String> = allAlphas + allNums
val allPuncts: List<String> = listOf("!", "\"", "#", """$""", "%", "&", "'", "(", ")",
				   "*", "+", ",", "-", ".", "/", ":", ";", "<", "=",
				   ">", "?", "@", "[", "\\", "]", "^", "_", "`",
				   "{", "|", "}", "~")

fun parseTokenPrefix(regexStr: String, offset: Int): String {
    val s = regexStr.substring(offset)
    if (s.length < 1) {
	return ""
    } else if (s.startsWith(alphaStar)) {
	return alphaStar
    } else if (s.startsWith(numStar)) {
	return numStar
    } else if (s.startsWith(alnumStar)) {
	return alnumStar
    } else if (s.startsWith(punctStar)) {
	return punctStar
    } else if (s.startsWith(dotStar)) {
	return dotStar
    } else {
	// Single character
	return s[0].toString()
    }
}

fun parseTokenSuffix(regexStr: String, offset: Int): String {
    val s: String = regexStr.substring(0, offset)
    if (s.length < 1) {
	return ""
    } else if (s.endsWith(alphaStar)) {
	return alphaStar
    } else if (s.endsWith(numStar)) {
	return numStar
    } else if (s.endsWith(alnumStar)) {
	return alnumStar
    } else if (s.endsWith(punctStar)) {
	return punctStar
    } else if (s.endsWith(dotStar)) {
	return dotStar
    } else {
	// Single character
	return s[s.length - 1].toString()
    }
}
