from ParSY.Rules import Hole, TerminalRule, ABRule


class Solver:
    """Implementation of the Enumerative Search to solve the Sketch problem.
    It is based on the SyGuS paradime, the grammar use to composed a CFG
    is based on Chomsky Normal Format. Meaning it uses this Syntax to
    guide the search:

        R -> <Terminal>
        R -> Rt | Rs

    Where Rt and Rs are other rules of the grammar and Rt and Rs must be
    different from Rule R. This solver won't build a recursive Rule.

    Parameters
    ----------
    rules: [Rule]
        The sketch rules.

    holes: [Hole]
        The holes to be filled

    Attributes
    ----------
    rules: [Rule]
        The rules being infered by the solver.

    """
    def __init__(self, rules, holes):
        self.rules, holes, = (rules, holes)
        for r in rules.values():
            print(r)
        print("--------------")
        self.nextRuleRID = len(self.rules)
        self.states = {h: 0 for h in holes}

    def _getNewHole(self):
        """Adds a new hole to the hole list.

        Returns
        -------
        Hole: hole added to the holes list.
        """
        hole = Hole(self.nextRuleRID)
        self.rules[self.nextRuleRID] = hole
        self.states[hole] = 0
        self.nextRuleRID += 1
        return hole

    def solveHole(self, hole, curState):
        """Evolves a Hole according to it's current state.
        The Hole can be into one of 3 states:
        0) H -> ??
        1) H -> <terminal>
        2) H -> RtRs

        The enumertive search tries state 1 and can evolve
        a Hole to state 2 if it is selected.

        Parameters
        ----------
        hole: Hole
            The hole to evolve it's state.

        curState: int
            The current state of the Hole.
        """
        if curState == 0:
            hole.rule = TerminalRule(self.nextRuleRID)
            self.rules[hole.rule.rid] = hole.rule
            self.nextRuleRID += 1

        elif curState == 1:
            newHole1 = self._getNewHole()
            self.solveHole(newHole1, self.states[newHole1])

            newHole2 = self._getNewHole()
            self.solveHole(newHole2, self.states[newHole2])

            hole.rule = ABRule(hole.rule.rid, newHole1, newHole2)
            self.rules[hole.rule.rid] = hole.rule

        self.states[hole] = curState + 1

    def cleanRule(self, rule):
        """Removes a rule from the grammar.

        Parameters
        ----------
        rule: Rule
            The rule to be removed.
        """
        if isinstance(rule, Hole):
            self.cleanRule(rule.rule)
            del self.states[rule]
            del self.rules[rule.rid]

        elif isinstance(rule, ABRule):
            self.cleanRule(rule.aRule)
            self.cleanRule(rule.bRule)
            del self.rules[rule.rid]

        elif isinstance(rule, TerminalRule):
            del self.rules[rule.rid]

    def solve(self, grammar):
        """Solve a sketch in order to it be capable of parsing a example string.

        Parameters
        ----------
        grammar: Grammar
            The sketch grammar to be solved.

        Returns
        -------
        {int: Rule}: grammar rules solved.
        """
        rules, solution = self._fillHoles(self.rules, self.states, grammar)

        rules = self._replaceTerminals(rules, solution)

        rules = self._removeHoles(rules, self.states.keys())
        return rules

    def _fillHoles(self, rules, states, grammar):
        """Fill the sketch holes using enumerative search.

        Parameters
        ----------
        rules: {int: Rule}
            The current rules to be filled.

        state: {Hole: int}
            The state of grammar holes.

        grammar: Grammar
            The grammar to be filled.

        Returns
        -------
        ({int: Rule}, [AST]): the rules filled, AST parsing the string.
        """
        for hole, state in states.copy().items():
            self.solveHole(hole, state)

        isParsed, solution = grammar.verify(rules[0])
        allParsed = grammar.allParsed()
        while not(isParsed and allParsed):
            if not allParsed:
                solution = reversed(solution)

            hole = None
            for s in solution:
                hole = self.rules[s[1]].getClosestHole()
                if hole is not None:
                    break
            self.solveHole(hole, states[hole])

            isParsed, solution = grammar.verify(rules[0])
            allParsed = grammar.allParsed()
        return rules, solution

    def _replaceTerminals(self, rules, solution):
        """Replace the terminal rules with the correct token to be matched.

        Parameters
        ----------
        rules: {int: Rule}
            The set of current Rules.

        solution: [(boolean, str, int, int)]
            The AST created by the grammar when the string was parsed.

        Returns
        -------
        {int: Rule}: The set Rules with terminals correctly replaced.
        """
        for s in solution:
            rule = rules[s[1]]
            rule.terminal = s[3]
        return rules

    def _removeHoles(self, rules, holes):
        """Remove the holes from the rules.

        Parameters
        ----------
        rules: {int: Rule}
            The set of current Rules.

        holes: [Hole]
            The holes to be replaced.

        Returns
        -------
        {int: Rule}: The set Rules without Holes.
        """
        for h in holes:
            h.rule.parent = h.parent
            h.rule.rid = h.rid
            rules[h.rid] = h.rule
            del rules[h.rid]
        rules = {h.rid: h for h in rules.values()}
        return rules

    def __repr__(self):
        resp = ""
        for rule in self.rules.values():
            resp += str(rule) + "\n"
        return resp
