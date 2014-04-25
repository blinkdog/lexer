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

/**
 * LexicalCharSequence is a performance decorator for CharSequence.
 * 
 * Starting in Java 7u6, the behavior of CharSequence.subSequence() was
 * changed from updating pointers into a backing sequence to making local
 * copies of the backing sequence. The reason cited is that there are some
 * use-cases where small (perhaps tiny!) CharSequence instances can retain
 * very large blocks of text. The example given was the values specified in
 * HTTP headers; a CharSequence retaining one small value specified in a
 * header, may in turn retain an entire HTTP request in memory.
 * 
 * In many applications, this memory hit is unacceptable. However, the
 * typical use-cases of a lexer call for the entire source text to be
 * retained in memory for the duration of execution. Therefore, this class
 * acts as a CharSequence decorator, bringing back the behavior of updating
 * pointers to a backing sequence.
 * 
 * Lexers want to chop the input into tokens. Retaining the entire source
 * text in memory is not an issue. An optimized subSequence() method is
 * <b>very much</b> an issue, as input.subSequence(tokenLength, input.length())
 * is a very common call.
 * 
 * @see <a href="http://bugs.java.com/bugdatabase/view_bug.do?bug_id=7197183">
 *      JDK-7197183 : (str) String.substring and String.subsequence performance
 *      slower since 7u6</a>
 * @see <a href="http://mail.openjdk.java.net/pipermail/core-libs-dev/2013-February/014609.html">
 *      FYC : JDK-7197183 : Improve copying behaviour of String.subSequence()</a>
 */
public class LexicalCharSequence implements CharSequence
{
    // TODO: javadoc
    public LexicalCharSequence(CharSequence source) {
        this.count = source.length();
        this.offset = 0;
        this.source = source;
    }
    
    // TODO: javadoc
    private LexicalCharSequence(CharSequence source, int offset, int count) {
        this.count = count;
        this.offset = offset;
        this.source = source;
    }
    
    // TODO: javadoc
    @Override
    public int length() {
        return count;
    }

    // TODO: javadoc
    @Override
    public char charAt(int index) {
        return source.charAt(index+offset);
    }

    // TODO: javadoc
    @Override
    public CharSequence subSequence(int start, int end) {
        if(start < 0) throw new IndexOutOfBoundsException();
        if(end < 0) throw new IndexOutOfBoundsException();
        if(end < start) throw new IndexOutOfBoundsException();
        if(end > count) throw new IndexOutOfBoundsException();
        return new LexicalCharSequence(source, offset+start, end-start);
    }

    // TODO: javadoc
    @Override
    public String toString() {
        return source.toString().substring(offset, offset+count);
    }

    // TODO: javadoc
    private final int count;

    // TODO: javadoc
    private final int offset;

    // TODO: javadoc
    private final CharSequence source;
}
