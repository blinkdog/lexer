/*
 * TokenTypeBuilderTest.java
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
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TokenTypeBuilderTest
{
    public TokenTypeBuilderTest() {
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
    public void testConstructor() {
        TokenTypeBuilder tokenTypeBuilder = new TokenTypeBuilder();
        assertNotNull(tokenTypeBuilder);
    }
    
    @Test
    public void testCreateEmpty() {
        try {
            TokenType tokenType = new TokenTypeBuilder().create();
            fail();
        } catch(NullPointerException e) {
            // expected
        }
    }
    
    @Test
    public void testPattern() {
        TokenType tokenType = new TokenTypeBuilder()
                  .pattern("[1-9][0-9]+")
                  .create();
        assertNotNull(tokenType);
        Pattern pattern = tokenType.getPattern();
        assertNotNull(pattern);
        assertTrue(pattern.matcher("2001").matches());
        assertTrue(pattern.matcher("487598798759876598749874745").matches());
        assertFalse(pattern.matcher("Exterminate!").matches());
        assertFalse(pattern.matcher("01010101").matches());
    }

    @Test
    public void testCanonical() {
        TokenType tokenType = new TokenTypeBuilder()
                  .pattern("a\u030A")
                  .create();
        assertNotNull(tokenType);
        Pattern pattern = tokenType.getPattern();
        assertNotNull(pattern);
        assertFalse(pattern.matcher("\u00E5").matches());

        TokenType canonicalType = new TokenTypeBuilder()
                  .pattern("a\u030A")
                  .canonical()
                  .create();
        assertNotNull(canonicalType);
        Pattern canonicalPattern = canonicalType.getPattern();
        assertNotNull(canonicalPattern);
        assertTrue(canonicalPattern.matcher("\u00E5").matches());
    }

    @Test
    public void testCanonicalBoolean() {
        TokenType tokenType = new TokenTypeBuilder()
                  .pattern("a\u030A")
                  .canonical(false)
                  .create();
        assertNotNull(tokenType);
        Pattern pattern = tokenType.getPattern();
        assertNotNull(pattern);
        assertFalse(pattern.matcher("\u00E5").matches());

        TokenType canonicalType = new TokenTypeBuilder()
                  .pattern("a\u030A")
                  .canonical(true)
                  .create();
        assertNotNull(canonicalType);
        Pattern canonicalPattern = canonicalType.getPattern();
        assertNotNull(canonicalPattern);
        assertTrue(canonicalPattern.matcher("\u00E5").matches());
    }

    @Test
    public void testFlagCanonical() {
        TokenType tokenType = new TokenTypeBuilder()
                  .pattern("a\u030A")
                  .create();
        assertNotNull(tokenType);
        Pattern pattern = tokenType.getPattern();
        assertNotNull(pattern);
        assertFalse(pattern.matcher("\u00E5").matches());

        TokenType canonicalType = new TokenTypeBuilder()
                  .pattern("a\u030A")
                  .flag(Pattern.CANON_EQ)
                  .create();
        assertNotNull(canonicalType);
        Pattern canonicalPattern = canonicalType.getPattern();
        assertNotNull(canonicalPattern);
        assertTrue(canonicalPattern.matcher("\u00E5").matches());
    }

    @Test
    public void testIgnoreCase() {
        TokenType tokenType = new TokenTypeBuilder()
                  .pattern("wisconsin")
                  .create();
        assertNotNull(tokenType);
        Pattern pattern = tokenType.getPattern();
        assertNotNull(pattern);
        assertFalse(pattern.matcher("Wisconsin").matches());

        TokenType ignoreType = new TokenTypeBuilder()
                  .pattern("wisconsin")
                  .ignoreCase()
                  .create();
        assertNotNull(ignoreType);
        Pattern ignorePattern = ignoreType.getPattern();
        assertNotNull(ignorePattern);
        assertTrue(ignorePattern.matcher("Wisconsin").matches());
    }

    @Test
    public void testIgnoreCaseBoolean() {
        TokenType tokenType = new TokenTypeBuilder()
                  .pattern("wisconsin")
                  .ignoreCase(false)
                  .create();
        assertNotNull(tokenType);
        Pattern pattern = tokenType.getPattern();
        assertNotNull(pattern);
        assertFalse(pattern.matcher("Wisconsin").matches());

        TokenType ignoreType = new TokenTypeBuilder()
                  .pattern("wisconsin")
                  .ignoreCase(true)
                  .create();
        assertNotNull(ignoreType);
        Pattern ignorePattern = ignoreType.getPattern();
        assertNotNull(ignorePattern);
        assertTrue(ignorePattern.matcher("Wisconsin").matches());
    }

    @Test
    public void testFlagIgnoreCase() {
        TokenType tokenType = new TokenTypeBuilder()
                  .pattern("wisconsin")
                  .create();
        assertNotNull(tokenType);
        Pattern pattern = tokenType.getPattern();
        assertNotNull(pattern);
        assertFalse(pattern.matcher("Wisconsin").matches());

        TokenType ignoreType = new TokenTypeBuilder()
                  .pattern("wisconsin")
                  .flag(Pattern.CASE_INSENSITIVE)
                  .create();
        assertNotNull(ignoreType);
        Pattern ignorePattern = ignoreType.getPattern();
        assertNotNull(ignorePattern);
        assertTrue(ignorePattern.matcher("Wisconsin").matches());
    }

    @Test
    public void testLiterate() {
        String patternWithComments = "lda | # load accumulator\n"
                                   + "ldx | # load x register\n"
                                   + "ldy   # load y register\n";
        TokenType tokenType = new TokenTypeBuilder()
                  .pattern(patternWithComments)
                  .create();
        assertNotNull(tokenType);
        Pattern pattern = tokenType.getPattern();
        assertNotNull(pattern);
        assertFalse(pattern.matcher("lda").matches());
        assertFalse(pattern.matcher("ldx").matches());
        assertFalse(pattern.matcher("ldy").matches());

        TokenType literateType = new TokenTypeBuilder()
                  .pattern(patternWithComments)
                  .literate()
                  .create();
        assertNotNull(literateType);
        Pattern literatePattern = literateType.getPattern();
        assertNotNull(literatePattern);
        assertTrue(literatePattern.matcher("lda").matches());
        assertTrue(literatePattern.matcher("ldx").matches());
        assertTrue(literatePattern.matcher("ldy").matches());
    }

    @Test
    public void testLiterateBoolean() {
        String patternWithComments = "lda | # load accumulator\n"
                                   + "ldx | # load x register\n"
                                   + "ldy   # load y register\n";
        TokenType tokenType = new TokenTypeBuilder()
                  .pattern(patternWithComments)
                  .literate(false)
                  .create();
        assertNotNull(tokenType);
        Pattern pattern = tokenType.getPattern();
        assertNotNull(pattern);
        assertFalse(pattern.matcher("lda").matches());
        assertFalse(pattern.matcher("ldx").matches());
        assertFalse(pattern.matcher("ldy").matches());

        TokenType literateType = new TokenTypeBuilder()
                  .pattern(patternWithComments)
                  .literate(true)
                  .create();
        assertNotNull(literateType);
        Pattern literatePattern = literateType.getPattern();
        assertNotNull(literatePattern);
        assertTrue(literatePattern.matcher("lda").matches());
        assertTrue(literatePattern.matcher("ldx").matches());
        assertTrue(literatePattern.matcher("ldy").matches());
    }

    @Test
    public void testFlagLiterate() {
        String patternWithComments = "lda | # load accumulator\n"
                                   + "ldx | # load x register\n"
                                   + "ldy   # load y register\n";
        TokenType tokenType = new TokenTypeBuilder()
                  .pattern(patternWithComments)
                  .create();
        assertNotNull(tokenType);
        Pattern pattern = tokenType.getPattern();
        assertNotNull(pattern);
        assertFalse(pattern.matcher("lda").matches());
        assertFalse(pattern.matcher("ldx").matches());
        assertFalse(pattern.matcher("ldy").matches());

        TokenType literateType = new TokenTypeBuilder()
                  .pattern(patternWithComments)
                  .flag(Pattern.COMMENTS)
                  .create();
        assertNotNull(literateType);
        Pattern literatePattern = literateType.getPattern();
        assertNotNull(literatePattern);
        assertTrue(literatePattern.matcher("lda").matches());
        assertTrue(literatePattern.matcher("ldx").matches());
        assertTrue(literatePattern.matcher("ldy").matches());
    }
  
    @Test
    public void testDotAll() {
        String regex = "a.b";
        TokenType tokenType = new TokenTypeBuilder()
                  .pattern(regex)
                  .create();
        assertNotNull(tokenType);
        Pattern pattern = tokenType.getPattern();
        assertNotNull(pattern);
        assertFalse(pattern.matcher("a\nb").matches());

        TokenType tokenType2 = new TokenTypeBuilder()
                  .pattern(regex)
                  .dotAll()
                  .create();
        assertNotNull(tokenType2);
        Pattern pattern2 = tokenType2.getPattern();
        assertNotNull(pattern2);
        assertTrue(pattern2.matcher("a\nb").matches());
    }

    @Test
    public void testDotAllBoolean() {
        String regex = "a.b";
        TokenType tokenType = new TokenTypeBuilder()
                  .pattern(regex)
                  .dotAll(false)
                  .create();
        assertNotNull(tokenType);
        Pattern pattern = tokenType.getPattern();
        assertNotNull(pattern);
        assertFalse(pattern.matcher("a\nb").matches());

        TokenType tokenType2 = new TokenTypeBuilder()
                  .pattern(regex)
                  .dotAll(true)
                  .create();
        assertNotNull(tokenType2);
        Pattern pattern2 = tokenType2.getPattern();
        assertNotNull(pattern2);
        assertTrue(pattern2.matcher("a\nb").matches());
    }

    @Test
    public void testDotAllFlag() {
        String regex = "a.b";
        TokenType tokenType = new TokenTypeBuilder()
                  .pattern(regex)
                  .create();
        assertNotNull(tokenType);
        Pattern pattern = tokenType.getPattern();
        assertNotNull(pattern);
        assertFalse(pattern.matcher("a\nb").matches());

        TokenType tokenType2 = new TokenTypeBuilder()
                  .pattern(regex)
                  .flag(Pattern.DOTALL)
                  .create();
        assertNotNull(tokenType2);
        Pattern pattern2 = tokenType2.getPattern();
        assertNotNull(pattern2);
        assertTrue(pattern2.matcher("a\nb").matches());
    }

    @Test
    public void testLiteral() {
        String regex = "[0-9]+";
        TokenType tokenType = new TokenTypeBuilder()
                  .pattern(regex)
                  .create();
        assertNotNull(tokenType);
        Pattern pattern = tokenType.getPattern();
        assertNotNull(pattern);
        assertFalse(pattern.matcher("[0-9]+").matches());

        TokenType tokenType2 = new TokenTypeBuilder()
                  .pattern(regex)
                  .literal()
                  .create();
        assertNotNull(tokenType2);
        Pattern pattern2 = tokenType2.getPattern();
        assertNotNull(pattern2);
        assertTrue(pattern2.matcher("[0-9]+").matches());
    }

    @Test
    public void testLiteralBoolean() {
        String regex = "[0-9]+";
        TokenType tokenType = new TokenTypeBuilder()
                  .pattern(regex)
                  .literal(false)
                  .create();
        assertNotNull(tokenType);
        Pattern pattern = tokenType.getPattern();
        assertNotNull(pattern);
        assertFalse(pattern.matcher("[0-9]+").matches());

        TokenType tokenType2 = new TokenTypeBuilder()
                  .pattern(regex)
                  .literal(true)
                  .create();
        assertNotNull(tokenType2);
        Pattern pattern2 = tokenType2.getPattern();
        assertNotNull(pattern2);
        assertTrue(pattern2.matcher("[0-9]+").matches());
    }
    
    @Test
    public void testLiteralFlag() {
        String regex = "[0-9]+";
        TokenType tokenType = new TokenTypeBuilder()
                  .pattern(regex)
                  .create();
        assertNotNull(tokenType);
        Pattern pattern = tokenType.getPattern();
        assertNotNull(pattern);
        assertFalse(pattern.matcher("[0-9]+").matches());

        TokenType tokenType2 = new TokenTypeBuilder()
                  .pattern(regex)
                  .flag(Pattern.LITERAL)
                  .create();
        assertNotNull(tokenType2);
        Pattern pattern2 = tokenType2.getPattern();
        assertNotNull(pattern2);
        assertTrue(pattern2.matcher("[0-9]+").matches());
    }

    @Test
    public void testMultiLine() {
        String regex = "^dog$";
        TokenType tokenType = new TokenTypeBuilder()
                  .pattern(regex)
                  .create();
        assertNotNull(tokenType);
        Pattern pattern = tokenType.getPattern();
        assertNotNull(pattern);
        assertFalse(pattern.matcher("cat\ndog\nmouse").find());

        TokenType tokenType2 = new TokenTypeBuilder()
                  .pattern(regex)
                  .multiLine()
                  .create();
        assertNotNull(tokenType2);
        Pattern pattern2 = tokenType2.getPattern();
        assertNotNull(pattern2);
        assertTrue(pattern2.matcher("cat\ndog\nmouse").find());
    }

    @Test
    public void testMultiLineBoolean() {
        String regex = "^dog$";
        TokenType tokenType = new TokenTypeBuilder()
                  .pattern(regex)
                  .multiLine(false)
                  .create();
        assertNotNull(tokenType);
        Pattern pattern = tokenType.getPattern();
        assertNotNull(pattern);
        assertFalse(pattern.matcher("cat\ndog\nmouse").find());

        TokenType tokenType2 = new TokenTypeBuilder()
                  .pattern(regex)
                  .multiLine(true)
                  .create();
        assertNotNull(tokenType2);
        Pattern pattern2 = tokenType2.getPattern();
        assertNotNull(pattern2);
        assertTrue(pattern2.matcher("cat\ndog\nmouse").find());
    }

    @Test
    public void testFlagMultiLine() {
        String regex = "^dog$";
        TokenType tokenType = new TokenTypeBuilder()
                  .pattern(regex)
                  .create();
        assertNotNull(tokenType);
        Pattern pattern = tokenType.getPattern();
        assertNotNull(pattern);
        assertFalse(pattern.matcher("cat\ndog\nmouse").find());

        TokenType tokenType2 = new TokenTypeBuilder()
                  .pattern(regex)
                  .flag(Pattern.MULTILINE)
                  .create();
        assertNotNull(tokenType2);
        Pattern pattern2 = tokenType2.getPattern();
        assertNotNull(pattern2);
        assertTrue(pattern2.matcher("cat\ndog\nmouse").find());
    }

    @Test
    public void testUnicode() {
        String regex = "résumé";
        TokenType tokenType = new TokenTypeBuilder()
                  .pattern(regex)
                  .ignoreCase()
                  .create();
        assertNotNull(tokenType);
        Pattern pattern = tokenType.getPattern();
        assertNotNull(pattern);
        assertFalse(pattern.matcher("RÉSUMÉ").matches());

        TokenType tokenType2 = new TokenTypeBuilder()
                  .pattern(regex)
                  .ignoreCase()
                  .unicode()
                  .create();
        assertNotNull(tokenType2);
        Pattern pattern2 = tokenType2.getPattern();
        assertNotNull(pattern2);
        assertTrue(pattern2.matcher("RÉSUMÉ").matches());
    }

    @Test
    public void testUnicodeBoolean() {
        String regex = "résumé";
        TokenType tokenType = new TokenTypeBuilder()
                  .pattern(regex)
                  .ignoreCase()
                  .unicode(false)
                  .create();
        assertNotNull(tokenType);
        Pattern pattern = tokenType.getPattern();
        assertNotNull(pattern);
        assertFalse(pattern.matcher("RÉSUMÉ").matches());

        TokenType tokenType2 = new TokenTypeBuilder()
                  .pattern(regex)
                  .ignoreCase()
                  .unicode(true)
                  .create();
        assertNotNull(tokenType2);
        Pattern pattern2 = tokenType2.getPattern();
        assertNotNull(pattern2);
        assertTrue(pattern2.matcher("RÉSUMÉ").matches());
    }

    @Test
    public void testFlagUnicode() {
        String regex = "résumé";
        TokenType tokenType = new TokenTypeBuilder()
                  .pattern(regex)
                  .ignoreCase()
                  .create();
        assertNotNull(tokenType);
        Pattern pattern = tokenType.getPattern();
        assertNotNull(pattern);
        assertFalse(pattern.matcher("RÉSUMÉ").matches());

        TokenType tokenType2 = new TokenTypeBuilder()
                  .pattern(regex)
                  .ignoreCase()
                  .flag(Pattern.UNICODE_CASE)
                  .create();
        assertNotNull(tokenType2);
        Pattern pattern2 = tokenType2.getPattern();
        assertNotNull(pattern2);
        assertTrue(pattern2.matcher("RÉSUMÉ").matches());
    }
    
    @Test
    public void testResumeHere() {
        assertTrue(false);
    }
}
