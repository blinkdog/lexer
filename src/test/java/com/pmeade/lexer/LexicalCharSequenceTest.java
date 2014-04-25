/*
 * LexicalCharSequenceTest.java
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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class LexicalCharSequenceTest
{
    //                                              1         2         3         4   4
    //                                    012345678901234567890123456789012345678901234
    public static final String PANGRAM = "The quick brown fox jumps over the lazy dog.";
    
    private CharSequence charSeq;
    
    public LexicalCharSequenceTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        // test in CharSequence = String mode
//        charSeq = PANGRAM;
        // test in CharSequence = LexicalCharSequence mode
        charSeq = new LexicalCharSequence(PANGRAM);
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testAlwaysSucceed() {
        assertTrue(true);
    }
    
    @Test
    public void testCharAtNegative() {
        try {
            charSeq.charAt(-1);
            fail();
        } catch(IndexOutOfBoundsException e) {
            // expected
        }
    }

    @Test
    public void testCharAtTooLarge() {
        try {
            charSeq.charAt(charSeq.length());
            fail();
        } catch(IndexOutOfBoundsException e) {
            // expected
        }
    }

    @Test
    public void testCharAtEnd() {
        assertEquals('.', charSeq.charAt(charSeq.length()-1));
    }
    
    @Test
    public void testCharAtBeginning() {
        assertEquals('T', charSeq.charAt(0));
    }
    
    @Test
    public void testCharAtNoThrow() {
        for(int i=0; i<charSeq.length(); i++) {
            charSeq.charAt(i);
        }
    }
    
    @Test
    public void testLength() {
        assertEquals(44, charSeq.length());
    }

    @Test
    public void testToString() {
        assertEquals(PANGRAM, charSeq.toString());
    }

    @Test
    public void testSubSequenceNegativeStart() {
        try {
            charSeq.subSequence(-5, 22);
            fail();
        } catch(IndexOutOfBoundsException e) {
            // expected
        }
    }

    @Test
    public void testSubSequenceEndTooLarge() {
        try {
            charSeq.subSequence(0, 88);
            fail();
        } catch(IndexOutOfBoundsException e) {
            // expected
        }
    }

    @Test
    public void testSubSequenceEndNegative() {
        try {
            charSeq.subSequence(0, -5);
            fail();
        } catch(IndexOutOfBoundsException e) {
            // expected
        }
    }

    @Test
    public void testSubSequenceEndLessThanStart() {
        try {
            charSeq.subSequence(15, 10);
            fail();
        } catch(IndexOutOfBoundsException e) {
            // expected
        }
    }

    @Test
    public void testEmptySequence() {
        CharSequence subSequence = charSeq.subSequence(22, 22);
        assertEquals(0, subSequence.length());
        String string = subSequence.toString();
        assertTrue(string.isEmpty());
    }

    @Test
    public void testAllEmptySequences() {
        for(int i=0; i<PANGRAM.length(); i++) {
            CharSequence subSequence = charSeq.subSequence(i, i);
            assertEquals(0, subSequence.length());
            String string = subSequence.toString();
            assertTrue(string.isEmpty());
        }
    }
    
    @Test
    public void testSubSequenceJustTooSmall() {
        try {
            charSeq.subSequence(-1, 22);
            fail();
        } catch(IndexOutOfBoundsException e) {
            // expected
        }
    }

    @Test
    public void testSubSequenceJustTooBig() {
        try {
            charSeq.subSequence(22, charSeq.length()+1);
            fail();
        } catch(IndexOutOfBoundsException e) {
            // expected
        }
    }
    
    @Test
    public void testSubSequenceBothJustOver() {
        try {
            charSeq.subSequence(-1, charSeq.length()+1);
            fail();
        } catch(IndexOutOfBoundsException e) {
            // expected
        }
    }

    @Test
    public void testSubSequenceBothTooBig() {
        try {
            charSeq.subSequence(charSeq.length()+1, charSeq.length()+1);
            fail();
        } catch(IndexOutOfBoundsException e) {
            // expected
        }
    }

    @Test
    public void testSubSequenceLastCall() {
        CharSequence subSeq = charSeq.subSequence(charSeq.length(), charSeq.length());
        assertEquals(0, subSeq.length());
        String subStr = subSeq.toString();
        assertTrue(subStr.isEmpty());
    }
    
    @Test
    public void testSubSequenceLongestPossible() {
        CharSequence subSequence = charSeq.subSequence(0, charSeq.length());
        assertEquals(charSeq.length(), subSequence.length());
        assertEquals(PANGRAM.length(), subSequence.length());
        String charSeqString = charSeq.toString();
        String subSequenceString = subSequence.toString();
        assertEquals(charSeqString, subSequenceString);
        assertEquals(PANGRAM, subSequenceString);
    }
    
    @Test
    public void testSubSequenceContent() {
        CharSequence subSequence = charSeq.subSequence(0, 19);
        assertEquals(19, subSequence.length());
        String subSequenceString = subSequence.toString();
        assertEquals("The quick brown fox", subSequenceString);
    }

    @Test
    public void testSubSequenceMulti() {
        CharSequence subSeq1 = charSeq.subSequence(0, 19);
        assertEquals(19, subSeq1.length());
        String subStr1 = subSeq1.toString();
        assertEquals("The quick brown fox", subStr1);
        
        CharSequence subSeq2 = subSeq1.subSequence(4, 15);
        assertEquals(11, subSeq2.length());
        String subStr2 = subSeq2.toString();
        assertEquals("quick brown", subStr2);

        CharSequence subSeq3 = subSeq2.subSequence(0, 5);
        assertEquals(5, subSeq3.length());
        String subStr3 = subSeq3.toString();
        assertEquals("quick", subStr3);

        CharSequence subSeq4 = subSeq2.subSequence(6, 11);
        assertEquals(5, subSeq4.length());
        String subStr4 = subSeq4.toString();
        assertEquals("brown", subStr4);
    }
    
    @Test
    public void testSubSequenceMultiChop() {
        CharSequence lastSeq = charSeq;
        boolean finished = false;
        while(finished == false) {
            CharSequence subSeq = lastSeq.subSequence(2, lastSeq.length());
            assertEquals(lastSeq.length()-2, subSeq.length());
            String lastStr = lastSeq.toString();
            String subStr = subSeq.toString();
            assertTrue(lastStr.endsWith(subStr));
            for(int i=0; i<subSeq.length(); i++) {
                assertEquals(lastSeq.charAt(i+2), subStr.charAt(i));
            }
            finished = (subSeq.length() < 2);
            lastSeq = subSeq;
        }
    }
}
