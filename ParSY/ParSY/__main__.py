from ParSY.Grammar import Grammar

grammars = []

# with open("examples.txt") as f:
#     for l in f:
g = Grammar("*<tokens> Query <query>")
g1 = Grammar("*<tokens>")
g.merge(g1)
# g1 = Grammar("<num> <time>-<num> Query <query>")
# g.merge(g1)
# g1 = Grammar("<num> <time>-<num> Query <query>")
# g.merge(g1)
# g1 = Grammar("<num> Connect <cnn-str> on")
# g.merge(g1)
for it in g.rules.values():
    print(it)
