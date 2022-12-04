package zhe.ParSy.Factory

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

public class RuleFactoryTest {
    @Test
    fun getTerminalRuleToString() {
        val maxRules = 1
        val factory = RuleFactory(maxRules)
        val rule = factory.getTerminalRule("token1", false)
        assertEquals("r0: <N>token1", rule.toString())
    }

    @Test
    fun getABRuleToString() {
        val maxRules = 1
        val factory = RuleFactory(maxRules)
        val rule = factory.getABRule(1, 2)
        assertEquals("r0: r1 r2", rule.toString())
    }
}
