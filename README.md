### Abstract
The goal of this project is to design and implement techniques that support the fast obfuscation of literals in database queries. Such techniques must be extensible and agnostic to the underlying database technology. The original meaning of obfuscated queries must be recoverable by certified users.

### Core Definitions
**Definition [**_Event_**]**: Let M be a machine that produces an infinite sequence of characters s, and G be a grammar. A string event is the occurrence of a substring s' in s, where s' is recognized by G. G is called an event grammar.

**Examples [**_Event_**]**:
* Occurrence of a string in a log
* Badly spelled word in a text editor
* Compilable code snippet in a program editor

**Definition [**_Action_**]**: A transformation s' → r' that maps s' from G into r' from H, where H is a grammar. H is called the action grammar.<br>
**Problem 1**: given a set S of strings, synthesize a grammar G, that recognizes S.<br>
**Problem 2**: given a set T of transformations, e.g., s' → r', synthesize a program P that implements T.

### Contributions
1. An interface, based on examples, to describe string events and actions.
2. The implementation of a handler of string events produced by the JVM.
3. An application of this handler to redact SQL queries, to normalize outputs and to send error messages.


### Overview
There are different scenarios, such as logging and debugging, in which it is interesting to obfuscate SQL queries. An obfuscated query preserves the structure of the original search term, however, literals that could review sensitive information are hidden. For instance, below we see two SQL queries, the original term, and its obfuscated version:

Original query:</br>
`SELECT email, address FROM users WHERE first_name="John" AND last_name="Smith";`

redacted query:</br>
`SELECT email, address FROM users WHERE first_name=":masked1" AND last_name="\<string\>";`

A further example follows below:

Original query:</br>
`SELECT item FROM carts WHERE userid=(SELECT userid FROM sessions WHERE sessionid=4525927)`


Redacted query:</br>
`SELECT item FROM carts WHERE userid=(SELECT userid FROM sessions WHERE sessionid=9234245)`

To be useful in the craft of practical tools, such redaction techniques should be fast and invertible. By invertible, we mean that a certified user should be able to recover the original query, having access to its censored version. By fast, we mean that the computational cost of obfuscating queries should not be high enough to prevent the approach from being deployed in usable tools. The creation of such a framework asks for solutions to a few research questions, namely:

##### RQ1: how to specify which parts of a query to obscure?</br>
##### RQ2: how to ensure invertible obfuscation?

Different techniques can be used to answer RQ1. Examples include the use of a domain specific language, ranging over the SQL grammar, that lets users specify literals that must be obfuscated. A more interesting approach, however, would rely on program synthesis. This is a technique to construct programs satisfying some high-level specification. In our case, we could rely on examples of original and redacted queries to derive fast and invertible obfuscators. That would be something similar to what FleshFill does. Recent advances on artificial intelligence have raised considerably the quality and speed of modern program synthesizers, as a survey by Alex Polozov will explain.

Concerning research question 2, different approaches allows us to encode literals in a safe, fast and invertible way. The two immediate candidates, in this case, are stateless cryptography, and dictionaries. Cryptography has the advantage to be easier to use and protect, however, it leads to obfuscated queries that will be hard to read (assuming that easy-reading should be a plus). Dictionaries require storying the key-value table in a safe environment, but it might lead to queries that, even obfuscated, make sense to an instructed observer.
