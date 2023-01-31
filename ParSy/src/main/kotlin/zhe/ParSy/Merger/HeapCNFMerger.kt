package zhe.ParSy.Merger

import org.slf4j.LoggerFactory
import zhe.ParSy.Grammar.HeapCNFGrammar
import zhe.ParSy.Grammar.IGrammar
import zhe.ParSy.Grammar.MutableRulesMap
import zhe.ParSy.Grammar.Rule
import zhe.ParSy.Grammar.Rule.ProductionRule
import zhe.ParSy.Grammar.Rule.TerminalRule
import zhe.ParSy.Regex.Compressor
import kotlin.math.max

public class HeapCNFMerger : IMerger {

    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    companion object {
        // The compressor is used to compress grammar rules.
        private val compressor = Compressor.newBasic()
    }

    // compressRuleSet takes a set of rules as an input, and returns the
    // compressed version of these rules. By compressed we mean that we try to
    // merge the rules into regex expressions as much as possible (as long as
    // the merge result isn't the lattice top).
    fun compressRuleSet(rules: Set<Rule>): Set<Rule> {
        if (rules.size < 2) {
            return rules
        }

        logger.debug("Compressing rules $rules")

        var compressedRules = setOf<TerminalRule>()
        var otherRules = mutableSetOf<Rule>()
        var prevRegex = ""
        rules.forEach { rule ->
            logger.debug("In new iteration in outer forEach")

            if (rule !is TerminalRule) {
                otherRules.plusAssign(rule)
                return@forEach
            }

            if (compressedRules.size == 0) {
                logger.debug("Compressed rules is empty")
                compressedRules = setOf<TerminalRule>(rule)
                logger.debug("Built new compressed rules: ${compressedRules}")
                return@forEach
            }

            var newCompressedRules = mutableSetOf<TerminalRule>()
            var isSensitive = rule.isSensitive
            var curRegex = rule.pattern
            compressedRules.forEach { prevRule ->
                logger.debug(
                    "In new iteration in inner forEach. " +
                        "Previous rule: ${prevRule}"
                )
                prevRegex = prevRule.pattern
                val compressResult = compressor.compress(prevRegex, curRegex)
                if (compressResult.isTop) {
                    logger.debug("Went to top!")
                    newCompressedRules.plusAssign(prevRule)
                    return@forEach
                } else {
                    curRegex = compressResult.rule
                    isSensitive = isSensitive || prevRule.isSensitive
                }
            }
            newCompressedRules.plusAssign(TerminalRule(curRegex, isSensitive))

            compressedRules = newCompressedRules
        }

        logger.debug("Compressed rules: $compressedRules")
        logger.debug("Other rules: $otherRules")

        return compressedRules.union(otherRules)
    }

    override fun merge(grammar: IGrammar, other: IGrammar): IGrammar {
        val newRules: MutableRulesMap = MutableRulesMap()

        val numRules: Int = max(grammar.rules.entries.size, other.rules.entries.size)

        for (i in 0 until numRules) {
            val g1: ProductionRule = other.rules.getOrDefault(i, ProductionRule(i))
            val g2: ProductionRule = grammar.rules.getOrDefault(i, ProductionRule(i))

            val allRules: Set<Rule> = g1.rules.union(g2.rules)
            logger.debug("Rules before compression: $allRules")

            val compressedRules = compressRuleSet(allRules)
            logger.debug("Compressed rules: $compressedRules")

            newRules.put(i, ProductionRule(i, compressedRules))
        }

        return HeapCNFGrammar(newRules)
    }
}
