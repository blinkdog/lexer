/*
 * TokenTypeBuilder.java
 * Copyright 2014 Patrick Meade.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pmeade.lexer;

import java.util.regex.Pattern;

/**
 * TokenTypeBuilder is a convenience class for creating TokenType objects
 * using the fluent API of the Builder pattern.
 * 
 * The general pattern is this:
 * 
 * <code>
 * TokenType tokenType = new TokenTypeBuilder()
 * // .. chained calls to the builder go here ..
 *           .create(); // build the TokenType from function calls above
 * </code>
 * 
 * @see TokenType
 */
public class TokenTypeBuilder
{
    /**
     * Enables canonical equivalence.
     * 
     * When this flag is specified then two characters will be considered to
     * match if, and only if, their full canonical decompositions match. The
     * expression "\u030A", for example, will match the string "\u00E5" when
     * this flag is specified. By default, matching does not take canonical
     * equivalence into account.
     * 
     * Specifying this flag may impose a performance penalty.
     * @return TokenTypeBuilder for additional chained calls
     */
    public TokenTypeBuilder canonical()
    {
        flags |= Pattern.CANON_EQ;
        return this;
    }

    /**
     * Enables canonical equivalence.
     * 
     * When this flag is specified then two characters will be considered to
     * match if, and only if, their full canonical decompositions match. The
     * expression "\u030A", for example, will match the string "\u00E5" when
     * this flag is specified. By default, matching does not take canonical
     * equivalence into account.
     * 
     * Specifying this flag may impose a performance penalty.
     * @param apply if the canonical equivalence flag should be set or not
     * @return TokenTypeBuilder for additional chained calls
     */
    public TokenTypeBuilder canonical(boolean apply)
    {
        if(apply) {
            flags |= Pattern.CANON_EQ;
        } else {
            flags &= ~(Pattern.CANON_EQ);
        }
        return this;
    }

    /**
     * Create and return a TokenType object according to the method calls
     * that have been chained to this TokenTypeBuilder.
     * @return TokenType that was built by this TokenTypeBuilder
     */
    public TokenType create()
    {
        if((flags & Pattern.LITERAL) == Pattern.LITERAL) {
            if(staticText == null) {
                staticText = pattern;
            }
        }
        return new TokenType(name, pattern, flags, skipped, staticText);
    }

    /**
     * Enables dotall mode.
     * 
     * In dotall mode, the expression . matches any character, including a line
     * terminator. By default this expression does not match line terminators. 
     * @return TokenTypeBuilder for additional chained calls
     */
    public TokenTypeBuilder dotAll()
    {
        flags |= Pattern.DOTALL;
        return this;
    }

    /**
     * Enables dotall mode.
     * 
     * In dotall mode, the expression . matches any character, including a line
     * terminator. By default this expression does not match line terminators. 
     * @param apply if dotall mode should be set or not
     * @return TokenTypeBuilder for additional chained calls
     */
    public TokenTypeBuilder dotAll(boolean apply)
    {
        if(apply) {
            flags |= Pattern.DOTALL;
        } else {
            flags &= ~(Pattern.DOTALL);
        }
        return this;
    }

    /**
     * Instructs the TokenTypeBuilder that the TokenType under construction
     * is an emit token. That is, this token is significant to the semantics
     * of the program (i.e.: a keyword, literal integer, identifier, etc.) and
     * should not be skipped or suppressed, like whitespace or a comment.
     * @return TokenTypeBuilder for additional chained calls
     */
    public TokenTypeBuilder emit()
    {
        skipped = false;
        return this;
    }

    /**
     * Inform the TokenTypeBuilder if this TokenType under construction
     * is an emit token or not. That is, is this token is significant to
     * the semantics of the program (i.e.: a keyword, literal integer,
     * identifier, etc.)? Should the lexer emit it instead of skipping
     * or suppressing the token, as it might do with whitespace or a comment?
     * @param apply if the lexer should emit a Token from this TokenType
     * @return TokenTypeBuilder for additional chained calls
     */
    public TokenTypeBuilder emit(boolean apply)
    {
        skipped = !apply;
        return this;
    }

    /**
     * Specify a Pattern flag to be set. This method is for power-users who
     * are deeply familiar with the flag constants from Java's Pattern class.
     * For those users, it may be easier to specify:
     * 
     * <code>.flag(Pattern.CASE_INSENSITIVE)</code>
     * 
     * Than it is to specify:
     * 
     * <code>.ignoreCase()</code>
     * 
     * For those aren't familiar with the flag constants from Java's Pattern
     * class, it is recommended that you use the methods provided in this
     * class instead.
     * @param flag Pattern flag to be applied to the TokenType's pattern
     * @return TokenTypeBuilder for additional chained calls
     * @see java.util.regex.Pattern
     */
    public TokenTypeBuilder flag(int flag)
    {
        flags |= flag;
        return this;
    }

    /**
     * Enables case-insensitive matching.
     * 
     * By default, case-insensitive matching assumes that only characters in
     * the US-ASCII charset are being matched. Unicode-aware case-insensitive
     * matching can be enabled by specifying the UNICODE_CASE flag in
     * conjunction with this flag.
     * 
     * Specifying this flag may impose a slight performance penalty.
     * @return TokenTypeBuilder for additional chained calls
     */
    public TokenTypeBuilder ignoreCase()
    {
        flags |= Pattern.CASE_INSENSITIVE;
        return this;
    }

    /**
     * Enables case-insensitive matching.
     * 
     * By default, case-insensitive matching assumes that only characters in
     * the US-ASCII charset are being matched. Unicode-aware case-insensitive
     * matching can be enabled by specifying the UNICODE_CASE flag in
     * conjunction with this flag.
     * 
     * Specifying this flag may impose a slight performance penalty.
     * @param apply if case insensitive matching should be set or not
     * @return TokenTypeBuilder for additional chained calls
     */
    public TokenTypeBuilder ignoreCase(boolean apply)
    {
        if(apply) {
            flags |= Pattern.CASE_INSENSITIVE;
        } else {
            flags &= ~(Pattern.CASE_INSENSITIVE);
        }
        return this;
    }

    /**
     * Enables literal parsing of the pattern.
     * 
     * When this flag is specified then the input string that specifies the
     * pattern is treated as a sequence of literal characters. Metacharacters
     * or escape sequences in the input sequence will be given no special
     * meaning.
     * 
     * The flags CASE_INSENSITIVE and UNICODE_CASE retain their impact on
     * matching when used in conjunction with this flag. The other flags
     * become superfluous.
     * @return TokenTypeBuilder for additional chained calls
     */
    public TokenTypeBuilder literal()
    {
        flags |= Pattern.LITERAL;
        return this;
    }

    /**
     * Enables literal parsing of the pattern.
     * 
     * When this flag is specified then the input string that specifies the
     * pattern is treated as a sequence of literal characters. Metacharacters
     * or escape sequences in the input sequence will be given no special
     * meaning.
     * 
     * The flags CASE_INSENSITIVE and UNICODE_CASE retain their impact on
     * matching when used in conjunction with this flag. The other flags
     * become superfluous.
     * @param apply if literal parsing of the pattern should be set or not
     * @return TokenTypeBuilder for additional chained calls
     */
    public TokenTypeBuilder literal(boolean apply)
    {
        if(apply) {
            flags |= Pattern.LITERAL;
        } else {
            flags &= ~(Pattern.LITERAL);
        }
        return this;
    }
    
    /**
     * Permits whitespace and comments in pattern.
     * 
     * In this mode, whitespace is ignored, and embedded comments starting
     * with # are ignored until the end of a line.
     * @return TokenTypeBuilder for additional chained calls
     */
    public TokenTypeBuilder literate()
    {
        flags |= Pattern.COMMENTS;
        return this;
    }

    /**
     * Permits whitespace and comments in pattern.
     * 
     * In this mode, whitespace is ignored, and embedded comments starting
     * with # are ignored until the end of a line.
     * @param apply if literate mode should be set or not
     * @return TokenTypeBuilder for additional chained calls
     */
    public TokenTypeBuilder literate(boolean apply)
    {
        if(apply) {
            flags |= Pattern.COMMENTS;
        } else {
            flags &= ~(Pattern.COMMENTS);
        }
        return this;
    }

    /**
     * Enables multiline mode.
     * 
     * In multiline mode the expressions ^ and $ match just after or just
     * before, respectively, a line terminator or the end of the input
     * sequence. By default these expressions only match at the beginning
     * and the end of the entire input sequence.
     * @return TokenTypeBuilder for additional chained calls
     */
    public TokenTypeBuilder multiLine()
    {
        flags |= Pattern.MULTILINE;
        return this;
    }

    /**
     * Enables multiline mode.
     * 
     * In multiline mode the expressions ^ and $ match just after or just
     * before, respectively, a line terminator or the end of the input
     * sequence. By default these expressions only match at the beginning
     * and the end of the entire input sequence.
     * @param apply if multiline mode should be set or not
     * @return TokenTypeBuilder for additional chained calls
     */
    public TokenTypeBuilder multiLine(boolean apply)
    {
        if(apply) {
            flags |= Pattern.MULTILINE;
        } else {
            flags &= ~(Pattern.MULTILINE);
        }
        return this;
    }

    /**
     * Set the name of the TokenType's lexical class
     * @param name the name of the lexical class (i.e. "LPAREN", "RBRACKET",
     *             "INT_LITERAL", "DOLLAR", etc.)
     * @return TokenTypeBuilder for additional chained calls
     */
    public TokenTypeBuilder name(String name)
    {
        this.name = name;
        return this;
    }

    /**
     * Set the regular expression of the Pattern used to match input
     * for this TokenType.
     * @param pattern regular expression of the Pattern used to match input
     * for this TokenType
     * @return TokenTypeBuilder for additional chained calls
     */
    public TokenTypeBuilder pattern(String pattern)
    {
        this.pattern = pattern;
        return this;
    }

    /**
     * Instructs the TokenTypeBuilder that the TokenType under construction
     * is a skip token. That is, this token is not significant to the semantics
     * of the language (i.e.: a keyword, literal integer, identifier, etc.) and
     * should be skipped or suppressed. This is often used for whitespace or
     * comment tokens.
     * @return TokenTypeBuilder for additional chained calls
     */
    public TokenTypeBuilder skip()
    {
        skipped = true;
        return this;
    }

    /**
     * Instruct the TokenTypeBuilder if the TokenType under construction is
     * a skip token or not. Is token is significant to the semantics
     * of the language (i.e.: a keyword, literal integer, identifier, etc.),
     * or should it be skipped or suppressed? Skipping is often used for
     * whitespace or comment tokens.
     * @return TokenTypeBuilder for additional chained calls
     */
    public TokenTypeBuilder skip(boolean apply)
    {
        skipped = apply;
        return this;
    }

    /**
     * Specify the static text (flyweight text) of the TokenType under
     * construction. This is often used for very narrow lexical classes
     * that do not require the input text. For example, an INTEGER_LITERAL
     * token, you might be interested in the digits of that integer. However,
     * for an LPAREN token, it will always be "(".
     * 
     * This is a performance enhancement, allowing all the LPAREN tokens to
     * share the single instance of the "(" instead of creating one for each
     * "(" in the input text.
     * 
     * In cases like INTEGER_LITERAL, it is generally NOT appropriate to
     * use static (flyweight) text. Instead allow the Lexer to provide
     * the actual digits from the input.
     * 
     * @param staticText The static (flyweight) text of the TokenType under
     *                   construction
     * @return TokenTypeBuilder for additional chained calls
     */
    public TokenTypeBuilder staticText(String staticText)
    {
        this.staticText = staticText;
        return this;
    }

    /**
     * Enables Unicode-aware case folding.
     * 
     * When this flag is specified then case-insensitive matching, when
     * enabled by the CASE_INSENSITIVE flag, is done in a manner consistent
     * with the Unicode Standard. By default, case-insensitive matching
     * assumes that only characters in the US-ASCII charset are being matched.
     * 
     * Specifying this flag may impose a performance penalty.
     * @return TokenTypeBuilder for additional chained calls
     */
    public TokenTypeBuilder unicode()
    {
        flags |= Pattern.UNICODE_CASE;
        return this;
    }

    /**
     * Enables Unicode-aware case folding.
     * 
     * When this flag is specified then case-insensitive matching, when
     * enabled by the CASE_INSENSITIVE flag, is done in a manner consistent
     * with the Unicode Standard. By default, case-insensitive matching
     * assumes that only characters in the US-ASCII charset are being matched.
     * 
     * Specifying this flag may impose a performance penalty.
     * @param apply if the unicode case folding mode should be set or not
     * @return TokenTypeBuilder for additional chained calls
     */
    public TokenTypeBuilder unicode(boolean apply)
    {
        if(apply) {
            flags |= Pattern.UNICODE_CASE;
        } else {
            flags &= ~(Pattern.UNICODE_CASE);
        }
        return this;
    }

    /**
     * The Pattern flags to apply when compiling the Pattern object for
     * the TokenType.
     * @see java.util.regex.Pattern
     */
    private int flags;

    /**
     * The name of the lexical class represented by the TokenType under
     * construction by this TokenTypeBuilder.
     */
    private String name;

    /**
     * The regular expression used to match patterns for this TokenType.
     */
    private String pattern;

    /**
     * Flag: Should a Token generated from this TokenType be skipped/suppressed
     *       in the output of the Lexer? (i.e.: is it whitespace or a comment
     *       with no semantic meaning in the language?)
     */
    private boolean skipped;

    /**
     * Static text (flyweight text) to be shared between all instances of
     * Token objects generated from this TokenType. This is generally
     * appropriate for very narrow or literal lexical classes (i.e.: LPAREN
     * is always "("). However, it is generally NOT appropriate for complex
     * lexical classes like INTEGER_LITERAL, where one might be interested
     * in the actual digits that were matched by the Lexer.
     */
    private String staticText;
}
