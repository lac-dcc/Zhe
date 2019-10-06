import re


class Tokenizer:
    """ Class to deal with the tokenization and Compose types constructions.

    Attributes
    ----------
    types: {str: str}
        Dictionary containing the types and their definition.
    """
    def __init__(self):
        self.types = {'tokens': r'<\w+>',
                      'str': r'[A-Za-z_-]+',
                      'int': r'-?\d+',
                      'float': r'-?\d*\.\d+',
                      'operators': r'[^a-zA-Z\d\s><_]',
                      'separator': r'\s'}

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
            if t[0] != "separator" and t[0] != "$":
                candidate.append(t)
            else:
                if len(candidate) > 1:
                    new_type = r''
                    for p in candidate:
                        if p[0] == "operators":
                            new_type += p[-1]
                        else:
                            new_type += '{}'.format(p[0])
                    if new_type not in types_dic.values():
                        types_dic['compose_type{}'.format(len(types_dic))] = new_type
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
        for e in examples:
            aux = e
            for k, r in self.types.items():
                if k in ["operators", "separator"]:
                    continue
                if "<query>" in aux:
                    parts = aux.split("<query>")
                    aux = re.sub(r, '{}'.format(k), parts[0]) + "<query>" + re.sub(r, '{}'.format(k), parts[1])
                else:
                    aux = re.sub(r, '{}'.format(k), aux)
            tokens.append(aux)
        return tokens
