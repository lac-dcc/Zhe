package zhe.ParSy.Regex

fun testNodeInterval1To16() = Pair(1.toUInt(), 16.toUInt())
fun testNodeInterval1To17() = Pair(1.toUInt(), 17.toUInt())
fun testNodeInterval1To32() = Pair(1.toUInt(), 32.toUInt())
fun testNodeInterval1To33() = Pair(1.toUInt(), 33.toUInt())
fun testNodeInterval0To100() = Pair(0.toUInt(), 100.toUInt())

fun testNodeAlphasUpper() = Node(stringsToChars(allAlphasUpper).toSet(), testNodeInterval1To32())
fun testNodeAlphasLower() = Node(stringsToChars(allAlphasLower).toSet(), testNodeInterval1To32())
fun testNodeNums() = Node(stringsToChars(allNums).toSet(), testNodeInterval1To16())
fun testNodePuncts() = Node(stringsToChars(allPuncts).toSet(), testNodeInterval1To16())

fun testBaseNodes(): List<Node> = listOf(
    testNodeAlphasUpper(),
    testNodeAlphasLower(),
    testNodeNums(),
    testNodePuncts()
)

fun testDisjointNodes(): List<Pair<Node, Node>> = listOf(
    Pair(testNodePuncts(), testNodeAlphasUpper()),
    Pair(testNodePuncts(), testNodeAlphasLower()),
    Pair(testNodePuncts(), testNodeNums())
)
