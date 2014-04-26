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

// TODO: javadoc
public class TokenType
{
    // TODO: javadoc
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

    // TODO: javadoc
    public String getName() {
        return name;
    }
    
    // TODO: javadoc
    public Pattern getPattern() {
        return pattern;
    }

    // TODO: javadoc
    public boolean isSkipped() {
        return skipped;
    }

    // TODO: javadoc
    public String getStaticText() {
        return staticText;
    }
    
    // TODO: javadoc
    private final String name;
    
    // TODO: javadoc
    private final Pattern pattern;
    
    // TODO: javadoc
    private final boolean skipped;

    // TODO: javadoc
    private final String staticText;
}
