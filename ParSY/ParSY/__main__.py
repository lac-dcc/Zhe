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


def antlr_format_grammar(rules, types, operators):
    print("grammar ParSy;\n")
    print("parse\n : r0* EOF\n ;\n")

    for it in rules.values():
        print(str(it).replace("<query>", "'<query>'") + "\n")

    print("SPACES\n : [ \\u000B\\t\\r\\n] -> channel(HIDDEN)\n ; \n")
    print("DIGIT : [0-9];")
    print("STRING\n : [A-Za-z_-]+\n ;")
    print("INT\n : '-'?DIGIT+\n ;")
    print("FLOAT\n : '-'?DIGIT*'.'DIGIT+\n ;")

    for idx, op in enumerate(operators):
        if op == '\\*':
            op = "*"
        if op == '\'':
            op = '\\\''
        print("OPERATOR_{} \n : \'{}\'\n ;\n".format(idx, op))

    for k, v in list(types.items())[6:]:
        print("{} \n : {}\n ;\n".format(k, v.strip()))


# with open("examples.txt") as f:
#     for l in f:

tokenizer = Tokenizer()
examples = tokenizer(examples)

g = Grammar(examples[0])
for e in examples[1:]:
    g1 = Grammar(e)
    g.merge(g1)


antlr_format_grammar(g.rules, tokenizer.types, tokenizer.operators)
