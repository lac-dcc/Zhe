package zhe.ParSy.Solver

class TokenStack(
    private val tokens: List<String>,
    private val sensitiveTokenIndexes: Set<Int>
) {
    private var tokenIdx: Int

    init {
        tokenIdx = tokens.size - 1
    }

    // This stack only has the pop method, because we expect all the tokens to
    // be added in the constructor.
    //
    // pop() will throw an exception in case the stack is empty.
    fun pop(): Pair<String, Boolean> {
        val token = tokens[tokenIdx]
        val isSensitive = sensitiveTokenIndexes.contains(tokenIdx)
        tokenIdx--
        return Pair(token, isSensitive)
    }

    fun size(): Int {
        return tokenIdx + 1
    }

    fun isEmpty(): Boolean {
        return size() == 0
    }
}
