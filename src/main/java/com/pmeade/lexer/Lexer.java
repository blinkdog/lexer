/*
 * Lexer.java
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lexer is the lexical analysis engine. Construct a Lexer by providing a
 * List of TokenType objects in priority order, and a CharSequence representing
 * the input to be converted into lexical Token objects.
 * 
 * If you want to receive all tokens (even those that have been marked to
 * skip/suppress), call the method <code>nextNoSkip()</code>.
 * 
 * If you want to receive the next token in the lexical stream, call the
 * method <code>nextToken()</code>.
 * 
 * If you want to receive all of the tokens in a List, call the method
 * <code>scan()</code> or <code>scanNoSkip()</code>.
 * 
 * If you want to restart lexical analysis from the beginning of the input,
 * call the method <code>reset()</code>.
 * 
 * If the Lexer encounters an error, or the end of the input, it will
 * return a <code>null</code> instead of a Token. The <code>scan()</code>
 * and <code>scanNoSkip()</code> methods will not contain this null token.
 * 
 * To determine if the Lexer has encountered an error, call the method
 * <code>isError()</code>.
 */
public class Lexer
{
    /**
     * Construct a Lexer to perform lexical analysis on the provided
     * input using the provided token types.
     * @param spec List of TokenType objects used to lex the provided input
     * @param source input to be processed into lexical Token objects
     * @throws NullPointerException if either spec or source are null
     */
    public Lexer(List<TokenType> spec, CharSequence source)
    {
        // validate input
        if(spec == null) throw new NullPointerException();
        // cache important values, set up initial lexer state
        this.error = false;
        this.executorService = Executors.newCachedThreadPool();
        this.input = new LexicalCharSequence(source);
        this.position = 0;
        this.sequence = 0;
        this.source = source;
        this.spec = spec;
    }

    /**
     * Determine if the Lexer has encountered an error.
     * @return true, iff the lexer encountered an error while performing
     *         lexical analysis on the input, otherwise false
     */
    public boolean isError() {
        return error;
    }

    /**
     * Obtain the next lexical Token of the provided input. Tokens generated
     * from TokenType objects marked as skipped will not be returned by this
     * method, but will instead be suppressed for a non-skip Token.
     * @return Token representing the next lexical unit from the input. If
     *         the Lexer encounters an error or the end of the input, this
     *         method will return null.
     */
    public Token next() {
        // until we find something we can return to the caller
        while(true) {
            // find the next token
            Token emitToken = nextNoSkip();
            // if we've reached the end
            if(emitToken == null) {
                // return that we've reached the end
                return null;
            }
            // if we've obtained a non-skip token
            if(emitToken.getTokenType().isSkipped() == false) {
                // return that
                return emitToken;
            }
        }
    }
    
    /**
     * Obtain the next lexical Token of the provided input. Even Token objects
     * generated from a TokenType marked as skipped will be returned by this
     * method. 
     * @return Token representing the next lexical unit from the input. If
     *         the Lexer encounters an error or the end of the input, this
     *         method will return null.
     */
    public Token nextNoSkip() {
        // check if we've still got input
        if(error) { return null; }
        if(input.length() == 0) { return null; }
        // build up the scanners
        List<ScanResult> scanResults = new ArrayList();
        for(int i=0; i<spec.size(); i++) {
            scanResults.add(new ScanResult(i, spec.get(i), input));
        }
        // run the scan concurrently on multiple threads
        List<Future<ScanResult>> results;
        while(true) {
            try {
                results = executorService.invokeAll(scanResults);
                break;
            } catch(InterruptedException e) {
                // oops
            }
        }
        // if we didn't get any results at all
        if(results.isEmpty()) {
            // indicate that lexical analysis failed on the input
            error = true;
            // and return end-of-stream
            return null;
        }
        // sort the results by token priority
        Collections.sort(results, new Comparator<Future<ScanResult>>() {
            @Override
            public int compare(Future<ScanResult> o1, Future<ScanResult> o2)
            {
                try {
                    ScanResult sr1 = o1.get();
                    ScanResult sr2 = o2.get();
                    return sr1.getPriority() - sr2.getPriority();
                } catch(Exception e) {
                    return 0;
                }
            }
        });
        // sort the results by match length
        Collections.sort(results, new Comparator<Future<ScanResult>>() {
            @Override
            public int compare(Future<ScanResult> o1, Future<ScanResult> o2)
            {
                try {
                    ScanResult sr1 = o1.get();
                    ScanResult sr2 = o2.get();
                    int l1 = (sr1.isSuccess()) ? sr1.getTokenText().length() : -1;
                    int l2 = (sr2.isSuccess()) ? sr2.getTokenText().length() : -1;
                    return l2 - l1;
                } catch(Exception e) {
                    return 0;
                }
            }
        });
        // obtain the scan result with the longest match and highest priority
        Future<ScanResult> winner = results.get(0);
        ScanResult scanResult;
        try {
            scanResult = winner.get();
        } catch(Exception e) {
            throw new IllegalStateException(e);
        }
        // if the winner wasn't even a successful match
        if(scanResult.isSuccess() == false) {
            // indicate that lexical analysis failed on the input
            error = true;
            // and return end-of-stream
            return null;
        }
        // determine if we use the canonical text from the TokenType
        TokenType tokenType = scanResult.getTokenType();
        String tokenText = tokenType.getStaticText();
        if(tokenText == null) {
            // nope, we need the actual text that we scanned from the input
            tokenText = scanResult.getTokenText();
        }
        Token token = new Token(sequence, tokenType, tokenText, position);
        // trim the token's text off the front of the input
        input = input.subSequence(scanResult.getTokenText().length(), input.length());
        // update our position count
        position += scanResult.getTokenText().length();
        // update our sequence count
        sequence++;
        // return the winning token to the caller
        return token;
    }

    /**
     * Reset the state of the Lexer. Returns the Lexer to its initial state,
     * even after reaching an error or the end of the input.
     */
    public void reset()
    {
        this.error = false;
        this.input = new LexicalCharSequence(source);
        this.position = 0;
        this.sequence = 0;
    }

    /**
     * Obtain all of the lexical Token objects for the  provided input. Tokens
     * generated from TokenType objects marked as skipped will not be returned
     * by this method, but will instead be suppressed in the result List.
     * @return List of Token objects representing the lexical units of the
     *         provided input. The list will contain all the non-skip tokens
     *         but not the final null indicating error or end of input.
     */
    public List<Token> scan()
    {
        List<Token> tokens = new ArrayList();
        while(true) {
            Token token = next();
            if(token == null) break;
            tokens.add(token);
        }
        return tokens;
    }

    /**
     * Obtain all of the lexical Token objects for the  provided input. Tokens
     * generated from TokenType objects marked as skipped will be returned
     * by this method, included in the result List in order of appearance.
     * @return List of Token objects representing the lexical units of the
     *         provided input. The list will contain all of the Token objects,
     *         but not the final null indicating error or end of input.
     */
    public List<Token> scanNoSkip()
    {
        List<Token> tokens = new ArrayList();
        while(true) {
            Token token = nextNoSkip();
            if(token == null) break;
            tokens.add(token);
        }
        return tokens;
    }

    /**
     * Flag: Did the Lexer encounter an error during lexical analysis?
     */
    private boolean error;

    /**
     * Thread pool used to run Pattern matchers concurrently.
     */
    private final ExecutorService executorService;

    /**
     * Input to be divided into lexical Token objects. This is typically a
     * LexicalCharSequence and represents the current state of the input.
     * The character at position 0 is the next character to be analyzed.
     */
    private CharSequence input;

    /**
     * The current position of the next character to be analyzed by the Lexer.
     * This is tracked so that it can be provided to Token objects. This way
     * the consumer of the Token objects can determine to which part of the
     * original input the Token corresponds.
     */
    private int position;

    /**
     * A simple count of the number of Token objects generated by the Lexer.
     * This is tracked so that it can be provided to Token objects.
     */
    private int sequence;

    /**
     * The original input to be lexically analyzed. This was provided at
     * construction time.
     */
    private final CharSequence source;

    /**
     * The list of TokenType objects with which to perform the lexical
     * analysis. The order of the List is taken to be the priority order
     * of the TokenType. That is, if the Pattern matcher objects of two
     * TokenTypes come back with equal length matches, then the TokenType
     * appearing first in the list will take precedence.
     */
    private final List<TokenType> spec;
}

/**
 * ScanResult is a convenience class for Lexer. It represents a Callable
 * implementation for running a Pattern matcher against the current input.
 * Created in bulk, they can be easily dumped into a thread pool for
 * concurrent execution. Conveniently, the object stores the results of
 * its own execution for handling later.
 */
class ScanResult implements Callable<ScanResult>
{
    /**
     * Construct a ScanResult to lexically analyze input according to
     * a single TokenType.
     * @param priority the priority of the TokenType from the list
     * @param tokenType the TokenType to use for a Pattern
     * @param input the input to be lexically analyzed
     */
    public ScanResult(int priority, TokenType tokenType, CharSequence input) {
        this.priority = priority;
        this.input = input;
        this.tokenType = tokenType;
    }

    /**
     * Perform lexical analysis of a single TokenType against the current
     * input.
     * @return this ScanResult object, containing the results of execution
     * @throws Exception this should only throw InterruptedException, and only
     *         if the thread pool decides to do so.
     */
    @Override
    public ScanResult call() throws Exception {
        Pattern pattern = tokenType.getPattern();
        Matcher matcher = pattern.matcher(input);
        success = matcher.lookingAt();
        tokenText = (success) ? matcher.group() : null;
        return this;
    }

    /**
     * Obtain the priority of the ScanResult. This is equivalent to the
     * position of the TokenType on the List of TokenType objects provided
     * to the Lexer.
     * @return the priority of the ScanResult (lower is better)
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Obtain the portion of the input that matched the TokenType.
     * @return the actual text of the input, if it matched the TokenType's
     *         Pattern, otherwise null
     */
    public String getTokenText() {
        return tokenText;
    }

    /**
     * Obtain the TokenType to be used for the lexical analysis. This TokenType
     * was provided at construction time.
     * @return the TokenType used for the lexical analysis
     */
    public TokenType getTokenType() {
        return tokenType;
    }

    /**
     * Determine if the lexical analysis was successful. ScanResult calls
     * Matcher.lookingAt(), and this method returns the return value of
     * that call.
     * @return true, if the TokenType's Pattern matched the input, otherwise
     *         false
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * The current input to be matched during lexical analysis. This is
     * provided at construction time.
     */
    private final CharSequence input;

    /**
     * The priority of the TokenType as provided to the Lexer. This is provided
     * at construction time.
     */
    private final int priority;

    /**
     * Flag: Did the Matcher succeed in matching the input?
     */
    private boolean success;

    /**
     * The actual text from the input, matched by the Matcher. If the
     * Matcher was not successful, this is null.
     */
    private String tokenText;

    /**
     * TokenType to be used for lexical analysis of the input. This is provided
     * at construction time.
     */
    private final TokenType tokenType;
}
