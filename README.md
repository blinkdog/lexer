# lexer
A lexer generator with a fluent API and concurrent execution

## Background
[Lexical analysis](https://en.wikipedia.org/wiki/Lexical_analysis) is
the process of converting a sequence of characters into a sequence of
tokens. A program that performs lexical analysis is called a lexer.

More simply, a lexer is a program that groups together character
sequences that naturally go together. For example, the following
statement in a programming language:

    slope = rise / run;

A lexer looks at the sequence of characters:

    's' 'l' 'o' 'p' 'e' ' ' '=' ' ' 'r' 'i' 's' 'e' ...

And groups it into tokens:

    IDENTIFIER: "slope"
    WHITESPACE
    EQUALS
    WHITESPACE
    IDENTIFIER: "rise"
    WHITESPACE
    DIVIDE
    WHITESPACE
    IDENTIFIER: "run"
    SEMICOLON

Some lexers will suppress tokens without meaning, such as the WHITESPACE.
And thus would produce a list like this instead:

    IDENTIFIER: "slope"
    EQUALS
    IDENTIFIER: "rise"
    DIVIDE
    IDENTIFIER: "run"
    SEMICOLON

There are many applications for lexers. They are often the first stage
of writing a compiler and/or interpreter for a programming language. The
lexer groups the "words" of the language into language tokens, which 
are in turn passed along to a parser.

Even if the tokens are not passed along to a parser, a text editor or
Integrated Development Environment (IDE) might run a lexer in order
to provide [syntax highlighting](https://en.wikipedia.org/wiki/Syntax_highlighting).

## Motivation
Several programs exist to create Lexer programs. The output of these
[lexer generators](https://en.wikipedia.org/wiki/Lexical_analysis#Lexer_generator)
is then incorporated into a larger program, such as a compiler or IDE.

Two things that always kind of nagged me about lexers...

1. Lexer generators (almost always) require a specification written in a
   domain-specific language.

That is, [JavaCC](https://en.wikipedia.org/wiki/JavaCC) will generate a lexer
for you, but you need to specify it in the language of JavaCC (a ".jj" file).

2. Lexers always seemed to be a single-threaded thing.

I must admit, I haven't done a deep examination of the lexer program
output by various lexer generators. It is possible that some use a
multi-threaded approach, or that multiple token classes are processed
simultaneously on the same table-based finite state machine.

My lack of research notwithstanding, the standard approach to lexers
leaves me feeling that there are units of work that might be broken
down and farmed off to other threads and/or cores.

## Solution
I decided to write a lexer with two goals in mind:

1. The lexical specification would be specified in plain old Java.

Java's core libraries have support for regular expressions. Since these
are the backbone of most lexer implementations anyway, it would seem
the building blocks for them have already been provided.

The [builder pattern](https://en.wikipedia.org/wiki/Builder_pattern) is
a natural fit to create a [fluent API](https://en.wikipedia.org/wiki/Fluent_interface)
for building lexical specifications.

2. The library provides an all-purpose lexer instead of a generator.

My lexer does not provide a means of creating a lexer program. Instead,
it, itself, is the lexer program. The Lexer class takes a lexical
specification (a List of TokenType objects) and a CharSequence as input,
and produces Token objects as output.

The design is intentionally naïve. Each lexical class (TokenType) is
run in parallel to see which one can produce the longest match. These
individual units of work are easy to farm off to a thread pool for
multi-threaded execution. A Token is generated from the winning
TokenType. This process repeats until the lexer encounters input
that it does not understand, or simply runs out of input.

## Example
Although a cliché in lexical specifications, we'll develop a Lexer
for simple mathematical expressions.

### Specification Sketch
We'd like to support the following symbols:

    + - * / ( )

And we'd like to have integer and floating point literals like so:

    0 1 5 10 121 300000 0.0 0.1 0.90909

And we'd like to be able to use whitespace freely. So the following
expression:

    (-5.0/2)

It should produce the same tokens as the following expression:

    (  -5.0
     /    2 )

### Mathematical Lexer
So we'll create a mathematical lexer.

    List<TokenType> TOKENS = new ArrayList();  // our lexical specification
    TOKENS.add(new TokenTypeBuilder()
        .name("PLUS")                          // Token: PLUS
        .pattern("+")                          //   a literal plus sign not
        .literal()                             //   a regular expression
        .create());
    TOKENS.add(new TokenTypeBuilder()
        .name("MINUS")                         // Token: MINUS
        .pattern("-")                          //   a literal minus sign not
        .literal()                             //   a regular expression
        .create());
    TOKENS.add(new TokenTypeBuilder()
        .name("MULTIPLY")                      // Token: MULTIPLY
        .pattern("*")                          //   a literal asterisk not
        .literal()                             //   a regular expression
        .create());
    TOKENS.add(new TokenTypeBuilder()
        .name("DIVIDE")                        // Token: DIVIDE
        .pattern("/")                          //   a literal forward slash
        .literal()                             //   not a regular expression
        .create());
    TOKENS.add(new TokenTypeBuilder()
        .name("LPAREN")                        // Token: LPAREN
        .pattern("(")                          //   a literal left parenthesis
        .literal()                             //   not a regular expression
        .create());
    TOKENS.add(new TokenTypeBuilder()
        .name("RPAREN")                        // Token: RPAREN
        .pattern(")")                          //   a literal right parenthesis
        .literal()                             //   not a regular expression
        .create());
    TOKENS.add(new TokenTypeBuilder()
        .name("FLOAT_LITERAL")                 // Token: FLOAT_LITERAL
        .pattern("[0-9]+\\.[0-9]+")            //   at least one number, dot,
        .create());                            //   and at least one more number
    TOKENS.add(new TokenTypeBuilder()
        .name("INTEGER_LITERAL")               // Token: INTEGER_LITERAL
        .pattern("([1-9][0-9]*)|(0)")          //   zero, or a sequence of
        .create());                            //   digits not starting with 0
    TOKENS.add(new TokenTypeBuilder()
        .name("WHITESPACE")                    // Token: WHITESPACE
        .pattern("\\s+")                       //   one or more whitespace chars
        .skip()                                // Skip/Suppress this token
        .create());

    Lexer lexer = new Lexer(TOKENS, "(((0.1 + 256) / (3.14 * 48)) - 5.0)");
    List<Token> tokens = lexer.scan();
    for(Token token : tokens) {
        System.out.println(token.toString());
    }

And the output is:

    [#0 @0 LPAREN "("]
    [#1 @1 LPAREN "("]
    [#2 @2 LPAREN "("]
    [#3 @3 FLOAT_LITERAL "0.1"]
    [#5 @7 PLUS "+"]
    [#7 @9 INTEGER_LITERAL "256"]
    [#8 @12 RPAREN ")"]
    [#10 @14 DIVIDE "/"]
    [#12 @16 LPAREN "("]
    [#13 @17 FLOAT_LITERAL "3.14"]
    [#15 @22 MULTIPLY "*"]
    [#17 @24 INTEGER_LITERAL "48"]
    [#18 @26 RPAREN ")"]
    [#19 @27 RPAREN ")"]
    [#21 @29 MINUS "-"]
    [#23 @31 FLOAT_LITERAL "5.0"]
    [#24 @34 RPAREN ")"]

### Notes
Remember that this is only lexical analysis. The Lexer does not (technically,
can not) determine things like the balance of parenthesis. For example, this
is a valid input for our mathematical expression lexer:

    (())))((

This is a bundle of unmatched parentheses with no semantic meaning. However, it
is not the job of the lexer to determine if the input "makes sense". The lexer
only breaks the input into a sequence of tokens. The meaning of the input (if
any) would be determined by other components after the lexer.

## More Examples
One of my goals is to add more (practical) examples to this lexer
module. I'd like to collect lexical specifications for various
programming languages, so this lexer module is useful right out
of the box for many applications.

This work of collecting/creating such specifications is on-going.

## Limitations
The Lexer is very simple. While this is a good quality, it does mean
that some complex lexical analysis is beyond the scope of this lexer.

For example, the following expression:

    Map<String,List<TokenType>>

When writing a compiler or interpreter, one would probably want the
above to be tokenized as:

    TYPE_NAME
    LESS_THAN
    TYPE_NAME
    COMMA
    TYPE_NAME
    LESS_THAN
    TYPE_NAME
    GREATER_THAN
    GREATER_THAN

This way a parser could identify a sequence like:

    LESS_THAN TYPE_NAME GREATER_THAN

And then it could infer the three belong to a parameterized type.

However, lexers usually take the largest chunk of input that it can
match as a token. So instead, the above will be usually be tokenized
as the following:

    TYPE_NAME
    LESS_THAN
    TYPE_NAME
    COMMA
    TYPE_NAME
    LESS_THAN
    TYPE_NAME
    RIGHT_SHIFT    <--  Oops, ">>" is mistaken for an operator!

More complex lexers like JavaCC include the ability to specify a
lexical state in lexical specifications. With that, parameterized
expressions like the above can be properly tokenized.

I may include this in a future version of this Lexer. However,
this version is still very functional and very simple. If you
don't have complex cases like the above in your input, it may
be perfect for your needs.

## License
This program is free software: you can redistribute it and/or modify
it under the terms of the [GNU Affero General Public License]
(https://www.gnu.org/licenses/agpl-3.0.html) as published by the
[Free Software Foundation](http://www.fsf.org/), either version 3
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
[GNU Affero General Public License]
(https://www.gnu.org/licenses/agpl-3.0.html) for more details.

You should have received a copy of the [GNU Affero General Public License]
(https://www.gnu.org/licenses/agpl-3.0.html) along with this program. If
not, see [http://www.gnu.org/licenses/](http://www.gnu.org/licenses/).
