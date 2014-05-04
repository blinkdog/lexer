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

## TokenTypeBuilder Fluent API
One of the main goals of this project was to allow the user to create
lexical specifications directly in Java, without the need for a
domain specific language.

The class TokenTypeBuilder provides a fluent API for constructing
the TokenType objects (i.e. lexical classes) that make up a lexical
specification.

This section provides documentation on the options provided by
TokenTypeBuilder for building TokenType objects:

### create()
This is the method that creates the TokenType from the specification
that has gone before. The following is legal Java. However, it will
throw an exception because the pattern has not has been specified.

    TokenType tokenType = new TokenTypeBuilder().create();

In 99.9999% of cases, create() will be the last call in a chain of
calls to TokenTypeBuilder.

### pattern()
This is *the* required element of building a TokenType. The lexer
needs to know how to identify sequences of characters that correspond
to the lexical class. This answers the question, "What kind of input
makes tokens for this type?"

    TokenType tokenType = new TokenTypeBuilder()
              .pattern("[1-9][0-9]*")
              .create();

Note that the [regular expression syntax](http://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html)
used here is the same as Java's Pattern class.

### canonical()
When this flag is specified then two characters will be considered to
match if, and only if, their full canonical decompositions match. The
expression "a\u030A", for example, will match the string "\u00E5" when
this flag is specified. By default, matching does not take canonical
equivalence into account.

    TokenType tokenType = new TokenTypeBuilder()
              .pattern("\u00E5")
              .canonical()
              .create();

This is exactly Pattern.CANON_EQ; another way to specify this, with
the exact same result is:

    TokenType tokenType = new TokenTypeBuilder()
              .pattern("\u00E5")
              .flag(Pattern.CANON_EQ)
              .create();

This second form is recommended only for those who are very familiar
with the Pattern class and use of regular expressions in Java.

### dotAll()
In dotall mode, the expression . matches any character, including a line
terminator. By default this expression does not match line terminators. 

    TokenType tokenType = new TokenTypeBuilder()
              .pattern(".")
              .dotAll()
              .create();

This is exactly Pattern.DOTALL; another way to specify this, with
the exact same result is:

    TokenType tokenType = new TokenTypeBuilder()
              .pattern(".")
              .flag(Pattern.DOTALL)
              .create();

This second form is recommended only for those who are very familiar
with the Pattern class and use of regular expressions in Java.

### emit()
Instructs the TokenTypeBuilder that the TokenType under construction
is an emit token. That is, this token is significant to the semantics
of the program (i.e.: a keyword, literal integer, identifier, etc.) and
should not be skipped or suppressed, like whitespace or a comment.

    TokenType tokenType = new TokenTypeBuilder()
              .pattern("\\s+")
              .emit()
              .create();

Technically, it is not necessary to specify this call. emit() is the
default for TokenType objects. Usually, this is only specified when
the author of the lexical specification wishes to be explicit about
the token being emitted. For example, some authors might call this
on a TokenType for WHITESPACE, to explicitly show that WHITESPACE
tokens are going to be emitted.

### flag()
Specify a flag from the Pattern class. For power-users who are very
familiar with Java's Pattern class, it is often easier to use this
method to specify a flag they know.

For those who don't know the regular expression flags from the
Pattern class, it is probably easier to use the methods provided in
the fluent API of TokenTypeBuilder instead.

Examples of the use of .flag() can be found in many other
subsections of the documentation, therefore it will be omitted here.

### ignoreCase()
Enables case-insensitive matching. By default, case-insensitive matching
assumes that only characters in the US-ASCII charset are being matched.
Unicode-aware case-insensitive matching can be enabled by specifying the
UNICODE_CASE flag in conjunction with this flag.

    TokenType tokenType = new TokenTypeBuilder()
              .pattern("wisconsin")
              .ignoreCase()
              .create();

This is exactly Pattern.CASE_INSENSITIVE; another way to specify this, with
the exact same result is:

    TokenType tokenType = new TokenTypeBuilder()
              .pattern("wisconsin")
              .flag(Pattern.CASE_INSENSITIVE)
              .create();

This second form is recommended only for those who are very familiar
with the Pattern class and use of regular expressions in Java.

### literal()
Enables literal parsing of the pattern.
 
When this flag is specified then the input string that specifies the
pattern is treated as a sequence of literal characters. Metacharacters
or escape sequences in the input sequence will be given no special
meaning.
 
The flags CASE_INSENSITIVE and UNICODE_CASE retain their impact on
matching when used in conjunction with this flag. The other flags
become superfluous.

    TokenType tokenType = new TokenTypeBuilder()
              .pattern("+")
              .literal()
              .create();

This is exactly Pattern.LITERAL; another way to specify this, with
the exact same result is:

    TokenType tokenType = new TokenTypeBuilder()
              .pattern("+")
              .flag(Pattern.LITERAL)
              .create();

This second form is recommended only for those who are very familiar
with the Pattern class and use of regular expressions in Java.

Two additional points to consider here.

1. For same cases, the call to literal() makes it very easy to
   specify an appropriate regular expression without the need
   to learn the esoteric escape sequences needed.

2. A call to literal() implies a call to staticText(). In 99.9999%
   of cases, this is desirable behavior. When using .ignoreCase()
   or .unicode(), you may get different results than you expect
   from the Token objects generated by the lexer.

### literate()
Permits whitespace and comments in pattern. In this mode, whitespace
is ignored, and embedded comments starting with # are ignored until
the end of a line.

    String patternWithComments = "lda | # load accumulator\n"
                               + "ldx | # load x register\n"
                               + "ldy   # load y register\n";
    TokenType tokenType = new TokenTypeBuilder()
              .pattern(patternWithComments)
              .literate()
              .create();

This is exactly Pattern.COMMENTS; another way to specify this, with
the exact same result is:

    String patternWithComments = "lda | # load accumulator\n"
                               + "ldx | # load x register\n"
                               + "ldy   # load y register\n";
    TokenType tokenType = new TokenTypeBuilder()
              .pattern(patternWithComments)
              .flag(Pattern.COMMENTS)
              .create();

This second form is recommended only for those who are very familiar
with the Pattern class and use of regular expressions in Java.

The name .literate() comes from the concept of
[literate programming](https://en.wikipedia.org/wiki/Literate_programming).

### multiLine()
In multiline mode the expressions ^ and $ match just after or just
before, respectively, a line terminator or the end of the input
sequence. By default these expressions only match at the beginning
and the end of the entire input sequence.

    String regex = "^dog$";
    TokenType tokenType = new TokenTypeBuilder()
              .pattern(regex)
              .multiLine()
              .create();

This is exactly Pattern.MULTILINE; another way to specify this, with
the exact same result is:

    String regex = "^dog$";
    TokenType tokenType = new TokenTypeBuilder()
              .pattern(regex)
              .flag(Pattern.MULTILINE)
              .create();

This second form is recommended only for those who are very familiar
with the Pattern class and use of regular expressions in Java.

### name()
Specify the name of the lexical class. This value isn't used by the
lexer, but it is supplied to the Token objects it creates. It
provides a human-readable tag for the type of the Token.

    TokenType tokenType = new TokenTypeBuilder()
              .pattern("[1-9][0-9]+")
              .name("INTEGER_LITERAL")
              .create();

Any Token objects created from this TokenType will also have the
name "INTEGER_LITERAL". It is recommended (although not required)
that you specify a .name() for all your TokenType objects in
your lexical specification.

### skip()
Instructs the TokenTypeBuilder that the TokenType under construction
is a skip token. That is, the token is not significant to the semantics
of the language and should be skipped or suppressed. Another way to put
it is that tokens of this TokenType are not keywords, literal integers,
identifiers, or something important to the *meaning* of the program.
This is often used for a TokenType that represents whitespace or a
comment.

    TokenType tokenType = new TokenTypeBuilder()
              .pattern("\\s+")
              .skip()
              .create();

### staticText()
Specify the static text (flyweight text) of the TokenType under
construction. This is often used for very narrow lexical classes
that do not require the input text. For example, an INTEGER_LITERAL
token, you might be interested in the digits of that integer. However,
for an LPAREN token, it will always be "(".
 
This is a performance enhancement, allowing all the LPAREN tokens to
share the single instance of the "(" instead of creating one for each
"(" in the input text.
 
In cases like INTEGER_LITERAL, it is generally NOT appropriate to
use static (flyweight) text. Instead allow the Lexer to provide
the actual digits from the input.

    String regex = "abstract";
    TokenType tokenType = new TokenTypeBuilder()
              .name("KEYWORD_ABSTRACT")
              .pattern(regex)
              .staticText(regex)
              .create();

Note that .literal() implies a call to .staticText(). The pattern
is also used as the static text for the token. If you would like
.literal() to use a different static text, you can also call
.staticText() to override, such as the following:

    TokenType tokenType = new TokenTypeBuilder()
              .name("KEYWORD_ABSTRACT")
              .pattern("abstract")
              .literal()
              .staticText("kw_abstract")
              .create();

### unicode()
When this flag is specified then case-insensitive matching, when
enabled by the CASE_INSENSITIVE flag, is done in a manner consistent
with the Unicode Standard. By default, case-insensitive matching
assumes that only characters in the US-ASCII charset are being matched.

    String regex = "résumé";
    TokenType tokenType = new TokenTypeBuilder()
              .pattern(regex)
              .ignoreCase()
              .unicode()
              .create();

This is exactly Pattern.UNICODE_CASE; another way to specify this, with
the exact same result is:

    String regex = "résumé";
    TokenType tokenType = new TokenTypeBuilder()
              .pattern(regex)
              .ignoreCase()
              .flag(Pattern.UNICODE_CASE)
              .create();

This second form is recommended only for those who are very familiar
with the Pattern class and use of regular expressions in Java.

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
