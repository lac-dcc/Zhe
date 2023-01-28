package zhe.ParSy.Regex

val testNodeInterval1To16 = Pair(1.toUInt(), 16.toUInt())
val testNodeInterval1To17 = Pair(1.toUInt(), 17.toUInt())
val testNodeInterval1To32 = Pair(1.toUInt(), 32.toUInt())
val testNodeInterval1To33 = Pair(1.toUInt(), 33.toUInt())
val testNodeInterval0To100 = Pair(0.toUInt(), 100.toUInt())

val testBaseNodes = listOf(
    Node(stringsToChars(allAlphasLower).toSet(), testNodeInterval1To32),
    Node(stringsToChars(allNums).toSet(), testNodeInterval1To16)
)
