from ParSY import Grammar
from ParSY import Tokenizer


# examples = ["Logging some <query>",
#             "Starting <query>",
#             "Warning <query>",
#             "error <query>",
#             "info about some <query>",
#             "any string <query>"]
examples = ["682 Query SELECT * FROM chefia WHERE id=1",
            "684 Init DB grossi 8and8 nan10nan a=1",
            "684 Init <query> banana=112121", 
            "121205 8:00:02 684 Query SET SESSION character_set_results='utf8'!",
            "685 Connect mysqldumpuser@localhost on"]


# with open("examples.txt") as f:
#     for l in f:

for e in examples:
    print(e)
print("------------")

tokenizer = Tokenizer()
examples = tokenizer(examples)

for e in examples:
    print(e)
print("------------")

g = Grammar(examples[0])
for e in examples[1:]:
    g1 = Grammar(e)
    g.merge(g1)


for it in g.rules.values():
    print(it)

for k, v in tokenizer.types.items():
    if k in ['operator', 'separator']:
        continue
    print("{} -> {}".format(k, v))
