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
        if((flags & Pattern.LITERAL) == Pattern.LITERAL) {
            if(staticText == null) {
                staticText = pattern;
            }
        }
        return new TokenType(name, pattern, flags, skipped, staticText);
    }

    // TODO: javadoc
    public TokenTypeBuilder dotAll()
    {
        flags |= Pattern.DOTALL;
        return this;
    }

    // TODO: javadoc
    public TokenTypeBuilder dotAll(boolean apply)
    {
        if(apply) {
            flags |= Pattern.DOTALL;
        } else {
            flags &= ~(Pattern.DOTALL);
        }
        return this;
    }

    // TODO: javadoc
    public TokenTypeBuilder emit()
    {
        skipped = false;
        return this;
    }

    // TODO: javadoc
    public TokenTypeBuilder emit(boolean apply)
    {
        skipped = !apply;
        return this;
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
    public TokenTypeBuilder literal()
    {
        flags |= Pattern.LITERAL;
        return this;
    }

    // TODO: javadoc
    public TokenTypeBuilder literal(boolean apply)
    {
        if(apply) {
            flags |= Pattern.LITERAL;
        } else {
            flags &= ~(Pattern.LITERAL);
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
    public TokenTypeBuilder multiLine()
    {
        flags |= Pattern.MULTILINE;
        return this;
    }

    // TODO: javadoc
    public TokenTypeBuilder multiLine(boolean apply)
    {
        if(apply) {
            flags |= Pattern.MULTILINE;
        } else {
            flags &= ~(Pattern.MULTILINE);
        }
        return this;
    }

    // TODO: javadoc
    public TokenTypeBuilder name(String name)
    {
        this.name = name;
        return this;
    }
    
    // TODO: javadoc
    public TokenTypeBuilder pattern(String pattern)
    {
        this.pattern = pattern;
        return this;
    }

    // TODO: javadoc
    public TokenTypeBuilder skip()
    {
        skipped = true;
        return this;
    }

    // TODO: javadoc
    public TokenTypeBuilder skip(boolean apply)
    {
        skipped = apply;
        return this;
    }

    // TODO: javadoc
    public TokenTypeBuilder staticText(String staticText)
    {
        this.staticText = staticText;
        return this;
    }
    
    // TODO: javadoc
    public TokenTypeBuilder unicode()
    {
        flags |= Pattern.UNICODE_CASE;
        return this;
    }

    // TODO: javadoc
    public TokenTypeBuilder unicode(boolean apply)
    {
        if(apply) {
            flags |= Pattern.UNICODE_CASE;
        } else {
            flags &= ~(Pattern.UNICODE_CASE);
        }
        return this;
    }

//    // TODO: javadoc
//    public TokenTypeBuilder unix()
//    {
//        flags |= Pattern.UNIX_LINES;
//        return this;
//    }
//
//    // TODO: javadoc
//    public TokenTypeBuilder unix(boolean apply)
//    {
//        if(apply) {
//            flags |= Pattern.UNIX_LINES;
//        } else {
//            flags &= ~(Pattern.UNIX_LINES);
//        }
//        return this;
//    }
    
    // TODO: javadoc
    private int flags;
    
    // TODO: javadoc
    private String name;
    
    // TODO: javadoc
    private String pattern;
    
    // TODO: javadoc
    private boolean skipped;
    
    // TODO: javadoc
    private String staticText;
}
