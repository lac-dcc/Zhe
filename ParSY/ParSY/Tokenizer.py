import re


class Tokenizer:
    """ Class to deal with the tokenization and Compose types constructions.

    Attributes
    ----------
    types: {str: str}
        Dictionary containing the types and their definition.
    """
    def __init__(self):
        self.types = {'TOKENS': r'<\w>+',
                      'STRING': r'[A-Za-z_-]+',
                      'INT': r'-?\d+',
                      'FLOAT': r'-?\d*\.\d+',
                      'operators': r'[^a-zA-Z\d\s><_]',
                      'separator': r'\s'}
        self.operators = []

    def _get_tokens(self, example, types_dic):
        """ Tokenize the the examples using the already existing types.


        Parameters
        ----------
        example: str
            The input string to tokenize.

        types_dic: {str: str}
            The current types and their definitions.


        Returns
        -------
        [(str, int, int, str)]: A list containing the tokenized string.
        """
        tokens_seq = []
        for k, r in types_dic.items():
            for data in re.finditer(r, example):
                text = example[data.start():data.end()]
                tokens_seq.append((k, *data.span(), text))
        return sorted(tokens_seq, key=lambda tup: tup[2]) + [("$", len(example), len(example) + 1, "$")]

    def _create_compose_types(self, tokens_seq, types_dic):
        """Function to combine the types into composing ones.

        Parameters
        ----------
        tokens_seq: [(str, int, int, str)]
            A list of input tokens.

        types_dic: {str: str}
            The current types and their definitions.
        """
        candidate = []
        for t in tokens_seq:

            if t[0] == "operators":
                if t[-1] not in self.operators:
                    op = t[-1]
                    if op == "*":
                        op = "\\*"
                    self.operators.append(op)

            if t[0] != "separator" and t[0] != "$":
                candidate.append(t)

            else:
                if len(candidate) > 1:
                    new_type = r''
                    for p in candidate:
                        if p[0] == "operators":
                            index = self.operators.index(p[-1])

                            op = "OPERATOR_{} ".format(index)
                            new_type += op
                        else:
                            new_type += '{} '.format(p[0])

                    if new_type not in types_dic.values():
                        types_dic['COMPOSE_TYPE_{}'.format(len(types_dic))] = new_type

                candidate.clear()

    def __call__(self, examples):
        """Tokenize the input examples.


        Parameters
        ----------
        examples: [str]
            A list of str to tokenize.

        Returns
        -------
        [str]: The input examples tokenized.
        """
        for e in examples:
            tokens_seq = self._get_tokens(e, self.types)
            self._create_compose_types(tokens_seq, self.types)

        tokens = []
        all_types = list(self.types.keys())
        for e in examples:
            aux = e
            for k in all_types[:4]:
                if "<query>" in aux:
                    parts = aux.split("<query>")
                    aux = re.sub(self.types[k], '{} '.format(k), parts[0]) + \
                          "<query>" + \
                          re.sub(self.types[k], '{} '.format(k), parts[1])

                else:
                    aux = re.sub(self.types[k], '{} '.format(k), aux)

            for i, op in enumerate(self.operators):
                aux = re.sub(op, '{}'.format('OPERATOR_{} '.format(i)), aux)

            for k in all_types[6:]:
                aux = re.sub(self.types[k], '{}'.format(k), aux)
            aux = re.sub(' +', ' ', aux)
            tokens.append(aux.strip())

                
        return tokens
