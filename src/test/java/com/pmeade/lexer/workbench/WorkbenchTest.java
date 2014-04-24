/*
 * WorkbenchTest.java
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

package com.pmeade.lexer.workbench;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class WorkbenchTest
{
    public WorkbenchTest() {
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

    /**
     * Literal patterns make it easy to create token patterns without
     * needing to learn the esoteric art of escaping complex regular
     * expressions.
     */
    @Test
    public void testPatternLiteral() {
        try {
            Pattern error = Pattern.compile("+");
            fail();
        } catch(PatternSyntaxException e) {
            // expected
        }
        Pattern ok = Pattern.compile("+", Pattern.LITERAL);
        Matcher m = ok.matcher("+");
        assertTrue(m.matches());
    }
    
    /**
     * Detection of partial matches allows us to determine if the matcher
     * completely failed to match, or might have matched, given a little
     * more input.
     */
    @Test
    public void testMatcherPartialMatch() {
        Pattern joe = Pattern.compile("Joe");
        Pattern joeBob = Pattern.compile("JoeBob");
        Pattern marySue = Pattern.compile("MarySue");
        
        String input = "Joe";
        
        Matcher matcherJoe = joe.matcher(input);
        Matcher matcherJoeBob = joeBob.matcher(input);
        Matcher matcherMarySue = marySue.matcher(input);
        
        assertTrue(matcherJoe.matches());
        assertFalse(matcherJoeBob.matches());
        assertFalse(matcherMarySue.matches());

        assertFalse(matcherJoe.hitEnd());
        assertTrue(matcherJoeBob.hitEnd());
        assertFalse(matcherMarySue.hitEnd());
    }

    /**
     * We can't have null patterns running around.
     */
    @Test
    public void testPatternNull() {
        try {
            Pattern p = Pattern.compile(null);
            fail();
        } catch(NullPointerException e) {
            // expected
        }
    }
    
    /**
     * Some tests to clarify the behavior of find().
     */
    @Test
    public void testMatcherFind() {
        Pattern joe = Pattern.compile("Joe");
        Pattern joeBob = Pattern.compile("JoeBob");
        Pattern marySue = Pattern.compile("MarySue");
        
        String input = "JoeBobMarySue";
        
        Matcher matcherJoe = joe.matcher(input);
        Matcher matcherJoeBob = joeBob.matcher(input);
        Matcher matcherMarySue = marySue.matcher(input);
        
        assertTrue(matcherJoe.find(0));
        assertTrue(matcherJoeBob.find(0));
        assertTrue(matcherMarySue.find(0));

        assertEquals(3, matcherJoe.end() - matcherJoe.start());
        assertEquals(6, matcherJoeBob.end() - matcherJoeBob.start());
        assertEquals(7, matcherMarySue.end() - matcherMarySue.start());

        assertEquals(0, matcherJoe.start());
        assertEquals(0, matcherJoeBob.start());
        assertEquals(6, matcherMarySue.start());
    }
    
    /**
     * Some tests to clarify the behavior of lookingAt().
     */
    @Test
    public void testMatcherLookingAt() {
        Pattern joe = Pattern.compile("Joe");
        Pattern joeBob = Pattern.compile("JoeBob");
        Pattern marySue = Pattern.compile("MarySue");
        
        String input = "JoeBobMarySue";
        
        Matcher matcherJoe = joe.matcher(input);
        Matcher matcherJoeBob = joeBob.matcher(input);
        Matcher matcherMarySue = marySue.matcher(input);
        
        assertTrue(matcherJoe.lookingAt());
        assertTrue(matcherJoeBob.lookingAt());
        assertFalse(matcherMarySue.lookingAt());

        assertEquals(0, matcherJoe.start());
        assertEquals(0, matcherJoeBob.start());
        assertEquals(3, matcherJoe.end() - matcherJoe.start());
        assertEquals(6, matcherJoeBob.end() - matcherJoeBob.start());

        CharSequence seq = input.subSequence(6, input.length());
        matcherMarySue = marySue.matcher(seq);
        assertTrue(matcherMarySue.lookingAt());
        assertEquals(0, matcherMarySue.start());
        assertEquals(7, matcherMarySue.end() - matcherMarySue.start());
    }
}
