package zhe.ParSy.Regex

fun collapseCharset(charset: Set<Char>): String {
    return charset.toList().joinToString(separator = "")
}
