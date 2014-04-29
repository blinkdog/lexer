/*
 * MathLexerTest.java
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

import com.pmeade.lexer.Lexer;
import com.pmeade.lexer.Token;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MathLexerTest
{
    public MathLexerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testAlwaysSucceed() {
        assertTrue(true);
    }
    
    @Test
    public void testMathLexer() {
        Lexer lexer = new Lexer(MathLexer.MATH_TOKENS, "((0.90909/3.14159)-(8*-3))+0");
        List<Token> tokens = lexer.scan();
        assertFalse(lexer.isError());
        assertEquals(16, tokens.size());
        assertEquals("LPAREN",          tokens.get( 0).getTokenType().getName());
        assertEquals("LPAREN",          tokens.get( 1).getTokenType().getName());
        assertEquals("FLOAT_LITERAL",   tokens.get( 2).getTokenType().getName());
        assertEquals("DIVIDE",          tokens.get( 3).getTokenType().getName());
        assertEquals("FLOAT_LITERAL",   tokens.get( 4).getTokenType().getName());
        assertEquals("RPAREN",          tokens.get( 5).getTokenType().getName());
        assertEquals("MINUS",           tokens.get( 6).getTokenType().getName());
        assertEquals("LPAREN",          tokens.get( 7).getTokenType().getName());
        assertEquals("INTEGER_LITERAL", tokens.get( 8).getTokenType().getName());
        assertEquals("MULTIPLY",        tokens.get( 9).getTokenType().getName());
        assertEquals("MINUS",           tokens.get(10).getTokenType().getName());
        assertEquals("INTEGER_LITERAL", tokens.get(11).getTokenType().getName());
        assertEquals("RPAREN",          tokens.get(12).getTokenType().getName());
        assertEquals("RPAREN",          tokens.get(13).getTokenType().getName());
        assertEquals("PLUS",            tokens.get(14).getTokenType().getName());
        assertEquals("INTEGER_LITERAL", tokens.get(15).getTokenType().getName());
    }
    
    @Test
    public void testMathLexer2() {
        Lexer lexer = new Lexer(MathLexer.MATH_TOKENS, "0+1+2+3+4+5");
        List<Token> tokens = lexer.scan();
        assertFalse(lexer.isError());
        assertEquals(11, tokens.size());
        assertEquals("INTEGER_LITERAL", tokens.get( 0).getTokenType().getName());
        assertEquals("PLUS",            tokens.get( 1).getTokenType().getName());
        assertEquals("INTEGER_LITERAL", tokens.get( 2).getTokenType().getName());
        assertEquals("PLUS",            tokens.get( 3).getTokenType().getName());
        assertEquals("INTEGER_LITERAL", tokens.get( 4).getTokenType().getName());
        assertEquals("PLUS",            tokens.get( 5).getTokenType().getName());
        assertEquals("INTEGER_LITERAL", tokens.get( 6).getTokenType().getName());
        assertEquals("PLUS",            tokens.get( 7).getTokenType().getName());
        assertEquals("INTEGER_LITERAL", tokens.get( 8).getTokenType().getName());
        assertEquals("PLUS",            tokens.get( 9).getTokenType().getName());
        assertEquals("INTEGER_LITERAL", tokens.get(10).getTokenType().getName());
    }

    @Test
    public void testMathLexer3() {
        Lexer lexer = new Lexer(MathLexer.MATH_TOKENS, "0.1+1.2+2.3+3.4+4.5+5.6");
        List<Token> tokens = lexer.scan();
        assertFalse(lexer.isError());
        assertEquals(11, tokens.size());
        assertEquals("FLOAT_LITERAL", tokens.get( 0).getTokenType().getName());
        assertEquals("PLUS",          tokens.get( 1).getTokenType().getName());
        assertEquals("FLOAT_LITERAL", tokens.get( 2).getTokenType().getName());
        assertEquals("PLUS",          tokens.get( 3).getTokenType().getName());
        assertEquals("FLOAT_LITERAL", tokens.get( 4).getTokenType().getName());
        assertEquals("PLUS",          tokens.get( 5).getTokenType().getName());
        assertEquals("FLOAT_LITERAL", tokens.get( 6).getTokenType().getName());
        assertEquals("PLUS",          tokens.get( 7).getTokenType().getName());
        assertEquals("FLOAT_LITERAL", tokens.get( 8).getTokenType().getName());
        assertEquals("PLUS",          tokens.get( 9).getTokenType().getName());
        assertEquals("FLOAT_LITERAL", tokens.get(10).getTokenType().getName());
    }
    
    @Test
    public void testMathLexer4() {
        String input = "(  -5.0 \n"
                     + " /    2 )\n";
        Lexer lexer = new Lexer(MathLexer.MATH_TOKENS, input);
        List<Token> tokens = lexer.scan();
        assertFalse(lexer.isError());
        assertEquals(6, tokens.size());
        assertEquals("LPAREN",          tokens.get( 0).getTokenType().getName());
        assertEquals("MINUS",           tokens.get( 1).getTokenType().getName());
        assertEquals("FLOAT_LITERAL",   tokens.get( 2).getTokenType().getName());
        assertEquals("DIVIDE",          tokens.get( 3).getTokenType().getName());
        assertEquals("INTEGER_LITERAL", tokens.get( 4).getTokenType().getName());
        assertEquals("RPAREN",          tokens.get( 5).getTokenType().getName());
    }

    @Test
    public void testMathLexer5() {
        Lexer lexer = new Lexer(MathLexer.MATH_TOKENS, "(((0.1 + 256) / (3.14 * 48)) - 5.0)");
        List<Token> tokens = lexer.scan();
        assertFalse(lexer.isError());
        for(Token token : tokens) {
            System.out.println(token.toString());
        }
    }
}
