/*
 * TokenType.java
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
 * TokenType represents a class of lexical unit. For example a sequence of
 * digits might represent a literal integer. The objects of this class
 * represent that lexical classification, containing a pattern to identify
 * matching tokens, as well as information about what to do with tokens
 * that do match.
 */
public class TokenType
{
    /**
     * Construct a TokenType. This is the full constructor for a class of
     * lexical token.
     * @param name the name of this lexical class (i.e. "LPAREN", "RPAREN", etc)
     * @param patternRegEx the regular expression of the Pattern to recognize
     *                     this type of lexical token
     * @param patternFlags the flags of the Pattern to recognize this type of
     *                     lexical token
     * @param skipped flag, indicating if these tokens should be skipped
     *                (suppressed) during lexical output, typically true for
     *                WHITESPACE or COMMENT lexical classes
     * @param staticText flyweight text, to be used instead of the actual input
     *                   text. this is used for literal keywords like "class"
     *                   or "abstract", or literal symbols like "%" or "{",
     *                   or other very narrow lexical classes. if not provided,
     *                   tokens generated from this type will use the actual
     *                   text matched from the input
     * @see java.util.regex.Pattern
     * @see TokenTypeBuilder
     */
    public TokenType(
            String name,
            String patternRegEx,
            int patternFlags,
            boolean skipped,
            String staticText)
    {
        this.name = name;
        this.pattern = Pattern.compile(patternRegEx, patternFlags);
        this.skipped = skipped;
        this.staticText = staticText;
    }

    /**
     * Obtain the name of this TokenType.
     * @return the name of this TokenType
     */
    public String getName() {
        return name;
    }

    /**
     * Obtain the Pattern of this TokenType.
     * @return the Pattern of this TokenType
     */
    public Pattern getPattern() {
        return pattern;
    }

    /**
     * Determine if a Token generated from this TokenType should be skipped
     * (suppressed) during the Lexer's output.
     * @return true, if a Token generated from this TokenType should be
     *         skipped by the Lexer's output, otherwise false.
     */
    public boolean isSkipped() {
        return skipped;
    }

    /**
     * Obtain the static text for this TokenType.
     * @return the static text of this TokenType
     */
    public String getStaticText() {
        return staticText;
    }

    /**
     * The name of this TokenType.
     */
    private final String name;

    /**
     * The Pattern of this TokenType.
     */
    private final Pattern pattern;

    /**
     * Flag: Should a Token generated from this TokenType by skipped
     *       (suppressed) by the output of the Lexer?
     */
    private final boolean skipped;

    /**
     * The static (flyweight) text of this TokenType.
     */
    private final String staticText;
}
