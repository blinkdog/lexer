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

// TODO: javadoc

import java.util.regex.Pattern;

public class TokenTypeBuilder
{
    // TODO: javadoc
    public TokenTypeBuilder canonical()
    {
        flags |= Pattern.CANON_EQ;
        return this;
    }

    // TODO: javadoc
    public TokenTypeBuilder canonical(boolean apply)
    {
        if(apply) {
            flags |= Pattern.CANON_EQ;
        } else {
            flags &= ~(Pattern.CANON_EQ);
        }
        return this;
    }
    
    // TODO: javadoc
    public TokenType create()
    {
        return new TokenType(pattern, flags);
    }

    // TODO: javadoc
    public TokenTypeBuilder flag(int flag)
    {
        flags |= flag;
        return this;
    }

    // TODO: javadoc
    public TokenTypeBuilder ignoreCase()
    {
        flags |= Pattern.CASE_INSENSITIVE;
        return this;
    }

    // TODO: javadoc
    public TokenTypeBuilder ignoreCase(boolean apply)
    {
        if(apply) {
            flags |= Pattern.CASE_INSENSITIVE;
        } else {
            flags &= ~(Pattern.CASE_INSENSITIVE);
        }
        return this;
    }

    // TODO: javadoc
    public TokenTypeBuilder literate()
    {
        flags |= Pattern.COMMENTS;
        return this;
    }

    // TODO: javadoc
    public TokenTypeBuilder literate(boolean apply)
    {
        if(apply) {
            flags |= Pattern.COMMENTS;
        } else {
            flags &= ~(Pattern.COMMENTS);
        }
        return this;
    }
    
    // TODO: javadoc
    public TokenTypeBuilder pattern(String pattern)
    {
        this.pattern = pattern;
        return this;
    }
    
    // TODO: javadoc
    private int flags;
    
    // TODO: javadoc
    private String pattern;
}
