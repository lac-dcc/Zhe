package zhe.ParSy.Regex

val alphaStar = "[a-zA-Z]*"
val numStar = "[0-9]*"
val alnumStar = "[a-zA-Z0-9]*"
val punctStar = "[\"!#\$%&'()*+,-./:;<>=?@\\[\\]^_`{}|~\\\\]*"
val dotStar = ".*"
val allStars: List<String> = listOf(
    alphaStar,
    numStar,
    alnumStar,
    punctStar,
    dotStar
)

val allAlphasLower: List<String> = listOf(
    "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
    "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
    "u", "v", "w", "x", "y", "z"
)
val allAlphasUpper: List<String> = listOf(
    "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
    "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
    "U", "V", "W", "X", "Y", "Z"
)
val allAlphas: List<String> = allAlphasLower + allAlphasUpper
val allNums: List<String> = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
val allAlnums: List<String> = allAlphas + allNums
val allPuncts: List<String> = listOf(
    "!", "\"", "#", """$""", "%", "&", "'", "(", ")",
    "*", "+", ",", "-", ".", "/", ":", ";", "<", "=",
    ">", "?", "@", "[", "\\", "]", "^", "_", "`",
    "{", "|", "}", "~"
)
val allChars = allAlnums + allPuncts

val starsToChars: Map<String, List<String>> = mapOf(
    alphaStar to allAlphas,
    numStar to allNums,
    alnumStar to allAlnums,
    punctStar to allPuncts,
    dotStar to allChars
)

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

// TODO: put everything under class Pattern. Create class Token in separate
// file.

class Pattern {
    companion object {
        fun tokenize(s: String): String {
            var tokenized = ""
            val tokens = split(s)
            tokens.forEach { token ->
                if (token.length == 1) {
                    tokenized += "[$token]"
                } else {
                    tokenized += "$token"
                }
            }
            tokenized = tokenized.trim()
            return tokenized
        }

        private fun split(s: String): List<String> {
            var offset = 0
            var tokens = mutableListOf<String>()
            while (offset < s.length) {
                val token = parseTokenPrefix(s, offset)
                tokens += token
                offset += token.length
            }
            return tokens
        }
    }
}
