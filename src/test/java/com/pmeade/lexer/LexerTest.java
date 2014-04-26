/*
 * LexerTest.java
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

import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LexerTest
{
    private static List<TokenType> asmTokens;
    private static void setUpAsmTokens() {
        asmTokens = new ArrayList();
        asmTokens.add(new TokenTypeBuilder()
            .name("COMMENT")
            .pattern(";.*$")
            .create());
        asmTokens.add(new TokenTypeBuilder()
            .name("WHITESPACE")
            .pattern("\\s+")
            .create());
        asmTokens.add(new TokenTypeBuilder()
            .name("COLON")
            .pattern(":")
            .literal()
            .create());
        asmTokens.add(new TokenTypeBuilder()
            .name("HEX_LITERAL")
            .pattern("\\$[0-9a-f]+")
            .ignoreCase()
            .create());
    }

    private static List<TokenType> calcTokens;
    private static void setUpCalcTokens() {
        calcTokens = new ArrayList();
        calcTokens.add(new TokenTypeBuilder()
            .name("LPAREN")
            .pattern("(")
            .literal()
            .create());
        calcTokens.add(new TokenTypeBuilder()
            .name("RPAREN")
            .pattern(")")
            .literal()
            .create());
        calcTokens.add(new TokenTypeBuilder()
            .name("PLUS")
            .pattern("+")
            .literal()
            .create());
        calcTokens.add(new TokenTypeBuilder()
            .name("INT_LITERAL")
            .pattern("[1-9][0-9]*")
            .create());
        calcTokens.add(new TokenTypeBuilder()
            .name("WHITESPACE")
            .pattern("\\s+")
            .create());
    }
    
    public LexerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        setUpCalcTokens();
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
    public void testLexer() {
        Lexer lexer = new Lexer(calcTokens, "(2+3)");
        
        Token token1 = lexer.next();
        assertNotNull(token1);
        TokenType tokenType1 = token1.getTokenType();
        assertNotNull(tokenType1);
        assertEquals("LPAREN", tokenType1.getName());
        
        Token token2 = lexer.next();
        assertNotNull(token2);
        TokenType tokenType2 = token2.getTokenType();
        assertNotNull(tokenType2);
        assertEquals("INT_LITERAL", tokenType2.getName());
        
        Token token3 = lexer.next();
        assertNotNull(token3);
        TokenType tokenType3 = token3.getTokenType();
        assertNotNull(tokenType3);
        assertEquals("PLUS", tokenType3.getName());
        
        Token token4 = lexer.next();
        assertNotNull(token4);
        TokenType tokenType4 = token4.getTokenType();
        assertNotNull(tokenType4);
        assertEquals("INT_LITERAL", tokenType4.getName());
        
        Token token5 = lexer.next();
        assertNotNull(token5);
        TokenType tokenType5 = token5.getTokenType();
        assertNotNull(tokenType5);
        assertEquals("RPAREN", tokenType5.getName());
    }
    
    @Test
    public void testNextNull() {
        Lexer lexer = new Lexer(calcTokens, "(2+3)");
        Token token1 = lexer.next();
        assertNotNull(token1);
        Token token2 = lexer.next();
        assertNotNull(token2);
        Token token3 = lexer.next();
        assertNotNull(token3);
        Token token4 = lexer.next();
        assertNotNull(token4);
        Token token5 = lexer.next();
        assertNotNull(token5);
        Token token6 = lexer.next();
        assertNull(token6);
    }
    
    @Test
    public void testReset() {
        Lexer lexer = new Lexer(calcTokens, "(2+3)");
        Token token1 = lexer.next();
        assertNotNull(token1);
        Token token2 = lexer.next();
        assertNotNull(token2);
        Token token3 = lexer.next();
        assertNotNull(token3);
        Token token4 = lexer.next();
        assertNotNull(token4);
        Token token5 = lexer.next();
        assertNotNull(token5);
        Token token6 = lexer.next();
        assertNull(token6);
        
        lexer.reset();
        
        token1 = lexer.next();
        assertNotNull(token1);
        token2 = lexer.next();
        assertNotNull(token2);
        token3 = lexer.next();
        assertNotNull(token3);
        token4 = lexer.next();
        assertNotNull(token4);
        token5 = lexer.next();
        assertNotNull(token5);
        token6 = lexer.next();
        assertNull(token6);
    }
    
    @Test
    public void testScan() {
        Lexer lexer = new Lexer(calcTokens, "(2+3)");
        List<Token> tokens = lexer.scan();
        assertNotNull(tokens);
        assertEquals(5, tokens.size());
    }

    @Test
    public void testDoubleScan() {
        Lexer lexer = new Lexer(calcTokens, "(2+3)");
        List<Token> tokens = lexer.scan();
        assertNotNull(tokens);
        assertEquals(5, tokens.size());
        lexer.reset();
        List<Token> tokens2 = lexer.scan();
        assertNotNull(tokens2);
        assertEquals(5, tokens2.size());
        for(int i=0; i<5; i++) {
            Token t1 = tokens.get(i);
            Token t2 = tokens2.get(i);
            assertFalse(t1 == t2);
            assertTrue(t1.equals(t2));
        }
    }

    @Test
    public void testTokenSequence() {
        Lexer lexer = new Lexer(calcTokens, "(2+3)");
        List<Token> tokens = lexer.scan();
        assertNotNull(tokens);
        assertEquals(5, tokens.size());
        for(int i=0; i<5; i++) {
            assertEquals(i, tokens.get(i).getSequence());
        }
    }
    
    @Test
    public void testTokenText() {
        Lexer lexer = new Lexer(calcTokens, "(2+3)");
        List<Token> tokens = lexer.scan();
        assertNotNull(tokens);
        assertEquals(5, tokens.size());
        assertEquals("(", tokens.get(0).getTokenText());
        assertEquals("2", tokens.get(1).getTokenText());
        assertEquals("+", tokens.get(2).getTokenText());
        assertEquals("3", tokens.get(3).getTokenText());
        assertEquals(")", tokens.get(4).getTokenText());
    }
    
    @Test
    public void testSkipTokens() {
        Lexer lexer = new Lexer(calcTokens, "( 2 + 3 )\n");
        List<Token> tokens = lexer.scan();
        assertNotNull(tokens);
        assertEquals(5, tokens.size());
        assertEquals("(", tokens.get(0).getTokenText());
        assertEquals("2", tokens.get(1).getTokenText());
        assertEquals("+", tokens.get(2).getTokenText());
        assertEquals("3", tokens.get(3).getTokenText());
        assertEquals(")", tokens.get(4).getTokenText());
    }
}
