from ParSY.Solver import Solver
from ParSY.Rules import Hole, TerminalRule, ABRule, CompositionRule


def getDefaultTemplate(hasQuery):
    """This function returns a default sketch to the grammar synthesis,
    it follows this structure:
        R0 -> R1R2
        R1 -> ??
        R2 -> R3 | R5
        R3 -> R4R5
        R5 -> ??
        R4 -> <query>

    Parameters
    ----------
    hasQuery : boolean
        It has a <query> string on the example ?
    """
    P = Hole(1)
    G = TerminalRule(4, terminal="<query>")
    E = Hole(5)
    T = ABRule(3, G, E)
    H = Hole(2, changable=False)
    S = ABRule(0, P, H)
    rules = {
        0: S,
        1: P,
        2: H,
        5: E
    }
    if hasQuery:
        H.rule = T
        rules[3] = T
        rules[4] = G
    else:
        H.rule = E
    holes = [P, E]
    return rules, holes


class Grammar(object):
    """Context Free Grammar(CFG) following Chomsky Normal Form.

    Parameters
    ----------
    string: str
        Example string used by the solver to derive the rules grammar rules.

    Attributes
    ----------
    tokens: [str]
        The tokens used to derive the grammar. Those represent the types present on the example.
    rukes: {int: str}
        A dictionary containing the rules for the grammar.
    """
    def __init__(self, string):
        self.tokens = string.split(" ")
        self.curToken = 0
        solver = Solver(*getDefaultTemplate('<query>' in self.tokens))
        self.rules = solver.solve(self)

    def merge(self, grammar):
        """Merge another CFG with self.


        Parameters
        ----------
        grammar: Grammar
            The grammar to be merged.
        """
        ids = set(self.rules.keys()) | set(grammar.rules.keys())
        for i in ids:
            r1 = self.rules.get(i, None)
            r2 = grammar.rules.get(i, None)
            if r1 is None:
                self.rules[i] = r2
            elif r2 is None:
                continue
            elif r1 != r2:
                if isinstance(r1, CompositionRule):
                    r1.addRule(r2)
                elif isinstance(r2, CompositionRule):
                    r2.add(r1)
                    self.rules[i] = r2
                elif str(r1) != str(r2):
                    self.rules[i] = CompositionRule(i, r1, r2)

    def verify(self, root):
        """Verify if a candidate grammar can parse the example string given to this grammar.

        Parameters
        ----------
        root: Rule
            The root rule for the set of candidates.

        Returns
        -------
        [(boolean, string, int)]: (was the parser sucessfull, the token matched, the toke position, the rule id used to match it).
        """
        self.curToken = 0
        return root.getNode(self.match)

    def allParsed(self):
        """Were all tokens consumed during the parser?


        Returns
        -------
        boolean: if all tokens were consumed.
        """
        return not self.curToken <= len(self.tokens)

    def match(self, token):
        """Match a token with the next token from the string.

        Parameters
        ----------
        token: str
            The token to be matched.

        Returns
        -------
        (boolean, str, int): Was the token matched, the token matched, the token position.
        """
        matchToken = self.tokens[self.curToken] if self.curToken < len(self.tokens) else "\\epsilon"

        if token == '<*>' or token == matchToken:
            self.curToken += 1
            return (True, (matchToken, self.curToken - 1))
        return (False, (matchToken, self.curToken))
