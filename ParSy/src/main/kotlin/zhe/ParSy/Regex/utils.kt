package zhe.ParSy.Regex

fun collapseCharset(charset: Set<Char>): String {
    return charset.toList().sorted().joinToString(separator = "")
}

fun stringsToChars(ss: List<String>): List<Char> {
    return ss.map { it[0] }
}
