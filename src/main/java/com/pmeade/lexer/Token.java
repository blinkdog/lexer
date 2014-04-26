/*
 * Token.java
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

import java.util.Objects;

// TODO: javadoc
public class Token
{
    // TODO: javadoc
    public Token(
            int sequence,
            TokenType tokenType,
            String tokenText,
            int position)
    {
        this.position = position;
        this.sequence = sequence;
        this.tokenText = tokenText;
        this.tokenType = tokenType;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + this.position;
        hash = 79 * hash + this.sequence;
        hash = 79 * hash + Objects.hashCode(this.tokenText);
        hash = 79 * hash + Objects.hashCode(this.tokenType);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Token other = (Token) obj;
        if (this.position != other.position) {
            return false;
        }
        if (this.sequence != other.sequence) {
            return false;
        }
        if (!Objects.equals(this.tokenText, other.tokenText)) {
            return false;
        }
        if (!Objects.equals(this.tokenType, other.tokenType)) {
            return false;
        }
        return true;
    }
    
    // TODO: javadoc
    public int getPosition() {
        return position;
    }
    
    // TODO: javadoc
    public int getSequence() {
        return sequence;
    }

    // TODO: javadoc
    public String getTokenText() {
        return tokenText;
    }

    // TODO: javadoc
    public TokenType getTokenType() {
        return tokenType;
    }

    // TODO: javadoc
    private final int position;
    
    // TODO: javadoc
    private final int sequence;

    // TODO: javadoc
    private final String tokenText;
    
    // TODO: javadoc
    private final TokenType tokenType;
}
