package zhe.ParSy.Regex

class Lexer {
    companion object {
        private fun reformat(s: String): String {
            // Assume the entire string is just plain characters.
            //
            // Example
            // reformat("abcd") -> "[a]{1}[b]{1}[c]{1}[d]{1}".
            //
            // Notice that the formatted string is 6x as long as the
            // non-formatted one.
            var formattedString = ""
            s.forEach {
                formattedString += "[$it]{1}"
            }
            return formattedString
        }

        fun tokenize(s: String): String {
            if (s.isEmpty()) {
                return s
            }
            if (s[0] != '[') {
                // If the first character of the string is not a [, then it is
                // not formatted as we expect. In this case, we must reformat
                // it.
                return reformat(s)
            }
            return s
        }
    }
}
