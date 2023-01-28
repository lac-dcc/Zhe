package zhe.ParSy.Regex

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LatticeTest {
    private val lattice = Lattice(testBaseNodes)

    @Test
    fun respectsIntervalLimitsEmptyNode() {
        assertTrue(lattice.respectsIntervalLimits(Node(setOf<Char>(), testNodeInterval1To32)))
    }

    @Test
    fun respectsIntervalLimitsNodeWithOneAlphaRespects() {
        assertTrue(lattice.respectsIntervalLimits(Node(setOf<Char>('a'), testNodeInterval1To32)))
    }

    @Test
    fun respectsIntervalLimitsNodeWithOneNumRespects() {
        assertTrue(lattice.respectsIntervalLimits(Node(setOf<Char>('1'), testNodeInterval1To16)))
    }

    @Test
    fun respectsIntervalLimitsNodeWithOneAlphaExcessive() {
        assertFalse(lattice.respectsIntervalLimits(Node(setOf<Char>('a'), testNodeInterval1To33)))
    }

    @Test
    fun respectsIntervalLimitsNodeWithOneNumExcessive() {
        assertFalse(lattice.respectsIntervalLimits(Node(setOf<Char>('1'), testNodeInterval1To17)))
    }

    @Test
    fun respectsIntervalLimitsNodeWithUnreasonablyWideInterval() {
        assertFalse(lattice.respectsIntervalLimits(Node(setOf<Char>('a'), testNodeInterval0To100)))
    }
}
