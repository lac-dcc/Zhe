package zhe.ParSy.Regex

// TODO: move charset stuff inside of a single class Charset
fun collapseCharset(charset: Set<Char>): String {
    return charset.toList().sorted().joinToString(separator = "")
}

fun stringsToChars(ss: List<String>): List<Char> {
    return ss.map { it[0] }
}

fun charsetFromS(s: String): Set<Char> {
    var cs = mutableSetOf<Char>()
    s.forEach {
        cs.add(it)
    }
    return cs
}
