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

// TODO: javadoc
public class Lexer
{
    // TODO: javadoc
    public Lexer(List<TokenType> spec, CharSequence source)
    {
        this.executorService = Executors.newCachedThreadPool();
        this.input = new LexicalCharSequence(source);
        this.source = source;
        this.spec = spec;
    }
    
    // TODO: javadoc
    public Token next() {
        // check if we've still got input
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
        // create a Token to return, based on the winner
        Future<ScanResult> winner = results.get(0);
        ScanResult scanResult;
        try {
            scanResult = winner.get();
        } catch(Exception e) {
            throw new IllegalStateException(e);
        }
        Token token = new Token(
                        sequence,
                        scanResult.getTokenType(),
                        scanResult.getTokenText());
        // trim the token's text off the front of the input
        input = input.subSequence(scanResult.getTokenText().length(), input.length());
        // update our sequence count
        sequence++;
        // return the winning token to the caller
        return token;
    }

    // TODO: javadoc
    public void reset()
    {
        this.input = new LexicalCharSequence(source);
        this.sequence = 0;
    }

    // TODO: javadoc
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
    
    // TODO: javadoc
    private final ExecutorService executorService;
    
    // TODO: javadoc
    private CharSequence input;
    
    // TODO: javadoc
    private int sequence;
    
    // TODO: javadoc
    private final CharSequence source;
    
    // TODO: javadoc
    private final List<TokenType> spec;
}

// TODO: javadoc
class ScanResult implements Callable<ScanResult>
{
    // TODO: javadoc
    public ScanResult(int priority, TokenType tokenType, CharSequence input) {
        this.priority = priority;
        this.input = input;
        this.tokenType = tokenType;
    }

    // TODO: javadoc
    @Override
    public ScanResult call() throws Exception {
        Pattern pattern = tokenType.getPattern();
        Matcher matcher = pattern.matcher(input);
        success = matcher.lookingAt();
        tokenText = (success) ? matcher.group() : null;
        return this;
    }

    // TODO: javadoc
    public int getPriority() {
        return priority;
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
    public boolean isSuccess() {
        return success;
    }
    
    // TODO: javadoc
    private final CharSequence input;

    // TODO: javadoc
    private final int priority;

    // TODO: javadoc
    private boolean success;

    // TODO: javadoc
    private String tokenText;

    // TODO: javadoc
    private final TokenType tokenType;
}
