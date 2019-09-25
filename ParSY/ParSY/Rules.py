from abc import ABC, abstractmethod


class Rule(ABC):
    """Abstract Class encoding a Rule for the CFG.


    Parameters
    ----------
    rid: int
        Rule ID.

    isHole: boolean
        Is this rule a Hole?

    Attributes
    ----------
    parent: Rule
        Parent rule.

    rid: int
        Rule ID.

    name: str
        Derivation rule name on the CFG.

    isHole: boolean
        Is this rule a Hole?
    """
    def __init__(self, rid, isHole=False):
        self.RID = rid
        self.hole = isHole
        self._parent = None

    @property
    def rid(self):
        return self.RID

    @rid.setter
    def rid(self, rid):
        self.RID = rid

    @property
    def parent(self):
        return self._parent

    @parent.setter
    def parent(self, rule):
        self._parent = rule

    @property
    def name(self):
        return "R{}".format(self.rid)

    @property
    def isHole(self):
        return self.hole

    def getClosestHole(self):
        """Find the closest Hole from a rule in the derivation tree.

        Returns
        -------
        object: The object Hole if exists else None.
        """
        if self.parent is not None:
            if self.isHole:
                return self
            return self.parent.getClosestHole()
        return None

    @abstractmethod
    def getNode(self, matchTokenMethod):
        """Gets the Node representing this rule in a derivation tree.

        Parameters
        ----------
        matchTokenMethod: function
            A function that matches the next token of a string with the token expected in the grammar.

        Returns
        -------
        [(boolean, string, int)]: (was the parser sucessfull, the token matched, the toke position, the rule id used to match it).
        """
        pass

    @abstractmethod
    def __repr__(self):
        pass


class ABRule(Rule):
    """A rule of the format R -> R1R2.


    Parameters
    ----------
    rid: int
        Rule ID.

    aRule: Rule
        R1 rule.

    bRule: Rule
        R2 rule.

    Attributes
    ----------
    aRule: Rule
        R1 rule.

    bRule: Rule
        R2 rule.
    """
    def __init__(self, rid, aRule, bRule):
        super().__init__(rid)
        self.aRule = aRule
        self.bRule = bRule
        self.aRule.parent = self
        self.bRule.parent = self

    def getNode(self, matchTokenMethod):
        match, tokenA = self.aRule.getNode(matchTokenMethod)
        if not match:
            return False, tokenA

        match, tokenB = self.bRule.getNode(matchTokenMethod)
        if not match:
            return(False, tokenB + tokenA)
        return(True, tokenA + tokenB)

    def __repr__(self):
        return "{} -> R{}R{}".format(self.name, self.aRule.rid, self.bRule.rid)


class TerminalRule(Rule):
    """A rule of the format R -> <token>.


    Parameters
    ----------
    rid: int
        Rule ID.

    terminal: string
        The terminal token expected to match.

    Attributes
    ----------
    terminal: string
        The terminal token expected to match.
    """
    def __init__(self, rid, terminal="<*>"):
        super().__init__(rid)
        self.terminal = terminal

    def getNode(self, matchTokenMethod):
        matched, token = matchTokenMethod(self.terminal)
        return(matched, [(matched, self.rid, self.terminal, *token)])

    def __repr__(self):
        return "{} -> {}".format(self.name, self.terminal)


class Hole(Rule):
    """A rule of the format R -> ??.


    Parameters
    ----------
    rid: int
        Rule ID.

    changable: boolean
        Can you change the value Rule of this Hole.

    Attributes
    ----------
    rule: Rule
        The Rule the Hole is pointing to.
    """
    def __init__(self, rid, changable=True):
        super().__init__(rid, isHole=changable)
        self.testRule = None

    @property
    def rule(self):
        return self.testRule

    @rule.setter
    def rule(self, rule):
        if self.testRule is not None:
            self.testRule.parent = None
        self.testRule = rule
        self.rule.parent = self

    def getNode(self, matchTokenMethod):
        if self.testRule is None:
            raise Exception("Parsing Error: Holes, must be solved before parsing a grammar")
        return self.rule.getNode(matchTokenMethod)

    def __repr__(self):
        if self.rule is not None:
            return "{} -> {}".format(self.name, self.rule.name)
        return "{} -> ??".format(self.name)


class CompositionRule(Rule):
    """A rule of the format R -> R1 | R2 | ... | RN .


    Parameters
    ----------
    rid: int
        Rule ID.

    rules: [Rule]
        The list of rules this Rule contains.

    Attributes
    ----------
    rules: [Rule]
        The list of rules this Rule contains.
    """
    def __init__(self, rid, *rules):
        super().__init__(rid)
        self.rules = list(rules)
        for r in rules:
            r.parent = self

    def addRule(self, rule):
        """ Adds a new Rule to the rules atribute.

        Parameters
        ----------
        rule: Rule
            The rule to be added to the Rules atribute.
        """
        strRule = str(rule)
        cmpArray = [strRule == str(r) for r in self.rules]
        if not any(cmpArray):
            self.rules.append(rule)

    def getNode(self, matchTokenMethod):
        wrongPaths = []
        for r in self.rules:
            matched, token = r.getNode(matchTokenMethod)
            if matched:
                return(True, token)
            else:
                wrongPaths.append(token)

        return(False, wrongPaths)

    def __repr__(self):
        strRules = str(self.rules[0]).split(' -> ')[1]
        for r in self.rules[1:]:
            strRules += " | " + str(r).split(' -> ')[1]
        return "R{} -> {}".format(self.rid, strRules)
