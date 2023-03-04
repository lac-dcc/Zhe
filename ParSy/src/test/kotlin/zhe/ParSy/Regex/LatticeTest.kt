package zhe.ParSy.Regex

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LatticeTest {
    private val psetlat = PowersetLattice(testBaseNodes(), testDisjointNodes())
    private val lattice = Lattice(testBaseNodes(), testDisjointNodes())

    @Test
    fun respectsIntervalLimitsEmptyNode() {
        assertTrue(lattice.respectsIntervalLimits(Node(setOf<Char>(), testNodeInterval1To32())))
    }

    @Test
    fun respectsIntervalLimitsNodeWithOneAlphaRespects() {
        assertTrue(lattice.respectsIntervalLimits(Node(setOf<Char>('a'), testNodeInterval1To32())))
    }

    @Test
    fun respectsIntervalLimitsNodeWithOneNumRespects() {
        assertTrue(lattice.respectsIntervalLimits(Node(setOf<Char>('1'), testNodeInterval1To16())))
    }

    @Test
    fun respectsIntervalLimitsNodeWithOneAlphaExcessive() {
        assertFalse(lattice.respectsIntervalLimits(Node(setOf<Char>('a'), testNodeInterval1To33())))
    }

    @Test
    fun respectsIntervalLimitsNodeWithOneNumExcessive() {
        assertFalse(lattice.respectsIntervalLimits(Node(setOf<Char>('1'), testNodeInterval1To17())))
    }

    @Test
    fun respectsIntervalLimitsNodeWithUnreasonablyWideInterval() {
        assertFalse(lattice.respectsIntervalLimits(Node(setOf<Char>('a'), testNodeInterval0To100())))
    }

    @Test
    fun disjointBaseNodesSameNodes() {
        val n1 = testNodeAlphasLower()
        val n2 = n1
        assertFalse(psetlat.areDisjoint(n1, n2))
    }

    @Test
    fun disjointBaseNodesDiffNodesNotDisjoint() {
        val n1 = testNodeAlphasLower()
        val n2 = testNodeAlphasUpper()
        assertFalse(psetlat.areDisjoint(n1, n2))
    }

    @Test
    fun disjointBaseNodesDiffNodesDisjointLower() {
        val n1 = testNodeAlphasLower()
        val n2 = testNodePuncts()
        assertTrue(psetlat.areDisjoint(n1, n2))
    }

    @Test
    fun disjointBaseNodesDiffNodesDisjointUpper() {
        val n1 = testNodeAlphasUpper()
        val n2 = testNodePuncts()
        assertTrue(psetlat.areDisjoint(n1, n2))
    }
}
