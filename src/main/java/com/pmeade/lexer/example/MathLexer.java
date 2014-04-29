/*
 * MathLexer.java
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

package com.pmeade.lexer.example;

import com.pmeade.lexer.TokenType;
import com.pmeade.lexer.TokenTypeBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MathLexer
{
    public static final List<TokenType> MATH_TOKENS;
    static {
        List<TokenType> tokenTypeList = new ArrayList();
        tokenTypeList.add(new TokenTypeBuilder()
            .name("PLUS")
            .pattern("+")
            .literal()
            .create());
        tokenTypeList.add(new TokenTypeBuilder()
            .name("MINUS")
            .pattern("-")
            .literal()
            .create());
        tokenTypeList.add(new TokenTypeBuilder()
            .name("MULTIPLY")
            .pattern("*")
            .literal()
            .create());
        tokenTypeList.add(new TokenTypeBuilder()
            .name("DIVIDE")
            .pattern("/")
            .literal()
            .create());
        tokenTypeList.add(new TokenTypeBuilder()
            .name("LPAREN")
            .pattern("(")
            .literal()
            .create());
        tokenTypeList.add(new TokenTypeBuilder()
            .name("RPAREN")
            .pattern(")")
            .literal()
            .create());
        tokenTypeList.add(new TokenTypeBuilder()
            .name("FLOAT_LITERAL")
            .pattern("[0-9]+\\.[0-9]+")
            .create());
        tokenTypeList.add(new TokenTypeBuilder()
            .name("INTEGER_LITERAL")
            .pattern("([1-9][0-9]*)|(0)")
            .create());
        tokenTypeList.add(new TokenTypeBuilder()
            .name("WHITESPACE")
            .pattern("\\s+")
            .skip()
            .create());
        MATH_TOKENS = Collections.unmodifiableList(tokenTypeList);
    }
}
