package org.apache.maven.shared.filtering;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Set;

import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.Interpolator;
import org.codehaus.plexus.interpolation.RecursionInterceptor;
import org.codehaus.plexus.interpolation.SimpleRecursionInterceptor;
import org.codehaus.plexus.interpolation.multi.DelimiterSpecification;

/**
 * A FilterReader implementation, that works with Interpolator interface instead of it's own interpolation
 * implementation. This implementation is heavily based on org.codehaus.plexus.util.InterpolationFilterReader.
 *
 * @author cstamas
 * @author Olivier Lamy
 * @since 1.0
 */
public class MultiDelimiterInterpolatorFilterReaderLineEnding
    extends AbstractFilterReaderLineEnding
{

    /**
     * Interpolator used to interpolate
     */
    private Interpolator interpolator;

    private RecursionInterceptor recursionInterceptor;

    /**
     * replacement text from a token
     */
    private String replaceData = null;

    /**
     * Index into replacement data
     */
    private int replaceIndex = 0;

    /**
     * Default begin token.
     */
    public static final String DEFAULT_BEGIN_TOKEN = "${";

    /**
     * Default end token.
     */
    public static final String DEFAULT_END_TOKEN = "}";

    /**
     * true by default to preserve backward comp
     */
    private boolean interpolateWithPrefixPattern = true;

    private String beginToken;

    private String endToken;

    private boolean supportMultiLineFiltering;

    private static final int MAXIMUM_BUFFER_SIZE = 8192;

    private boolean eof = false;

    /**
     * This constructor uses default begin token ${ and default end token }.
     *
     * @param in reader to use
     * @param interpolator interpolator instance to use
     * @param supportMultiLineFiltering If multi line filtering is allowed
     */
    public MultiDelimiterInterpolatorFilterReaderLineEnding( Reader in, Interpolator interpolator,
                                                             boolean supportMultiLineFiltering )
    {
        this( in, interpolator, new SimpleRecursionInterceptor(), supportMultiLineFiltering );
    }

    /**
     * @param in reader to use
     * @param interpolator interpolator instance to use
     * @param ri The {@link RecursionInterceptor} to use to prevent recursive expressions.
     * @param supportMultiLineFiltering If multi line filtering is allowed
     */
    public MultiDelimiterInterpolatorFilterReaderLineEnding( Reader in, Interpolator interpolator,
                                                             RecursionInterceptor ri,
                                                             boolean supportMultiLineFiltering )
    {
        // wrap our own buffer, so we can use mark/reset safely.
        super( new BufferedReader( in, MAXIMUM_BUFFER_SIZE ) );

        this.interpolator = interpolator;

        // always cache answers, since we'll be sending in pure expressions, not mixed text.
        this.interpolator.setCacheAnswers( true );

        recursionInterceptor = ri;

        delimiters.add( DelimiterSpecification.DEFAULT_SPEC );

        this.supportMultiLineFiltering = supportMultiLineFiltering;

        calculateMarkLength();

    }

    /**
     * @param delimiterSpec delimiter spec.
     * @return true/false.
     */
    public boolean removeDelimiterSpec( String delimiterSpec )
    {
        return delimiters.remove( DelimiterSpecification.parse( delimiterSpec ) );
    }

    /**
     * @param specs set of specs.
     * @return {@link MultiDelimiterInterpolatorFilterReaderLineEnding}
     */
    public AbstractFilterReaderLineEnding setDelimiterSpecs( Set<String> specs )
    {
        delimiters.clear();
        for ( String spec : specs )
        {
            delimiters.add( DelimiterSpecification.parse( spec ) );
            markLength += spec.length() * 2;
        }

        return this;
    }

    /**
     * Skips characters. This method will block until some characters are available, an I/O error occurs, or the end of
     * the stream is reached.
     *
     * @param n The number of characters to skip
     * @throws IllegalArgumentException If <code>n</code> is negative.
     * @throws IOException If an I/O error occurs
     * @return the number of characters actually skipped
     */
    public long skip( long n )
        throws IOException, IllegalArgumentException
    {
        if ( n < 0L )
        {
            throw new IllegalArgumentException( "skip value is negative" );
        }

        for ( long i = 0; i < n; i++ )
        {
            if ( read() == -1 )
            {
                return i;
            }
        }
        return n;
    }

    /**
     * Reads characters into a portion of an array. This method will block until some input is available, an I/O error
     * occurs, or the end of the stream is reached.
     *
     * @param cbuf Destination buffer to write characters to. Must not be <code>null</code>.
     * @param off Offset at which to start storing characters.
     * @param len Maximum number of characters to read.
     * @return the number of characters read, or -1 if the end of the stream has been reached
     * @throws IOException If an I/O error occurs
     */
    public int read( char cbuf[], int off, int len )
        throws IOException
    {
        for ( int i = 0; i < len; i++ )
        {
            int ch = read();
            if ( ch == -1 )
            {
                if ( i == 0 )
                {
                    return -1;
                }
                else
                {
                    return i;
                }
            }
            cbuf[off + i] = (char) ch;
        }
        return len;
    }

    /**
     * Returns the next character in the filtered stream, replacing tokens from the original stream.
     *
     * @return the next character in the resulting stream, or -1 if the end of the resulting stream has been reached
     * @throws IOException if the underlying stream throws an IOException during reading
     */
    public int read()
        throws IOException
    {
        if ( replaceIndex > 0 )
        {
            return replaceData.charAt( replaceData.length() - ( replaceIndex-- ) );
        }
        if ( eof )
        {
            return -1;
        }

        BoundedReader in = new BoundedReader( this.in, markLength );

        int ch = in.read();
        if ( ch == -1 || ( ch == '\n' && !supportMultiLineFiltering ) )
        {
            return ch;
        }

        boolean inEscape = useEscape && ch == getEscapeString().charAt( 0 );

        StringBuilder key = new StringBuilder();

        // have we found an escape string?
        if ( inEscape )
        {
            for ( int i = 0; i < getEscapeString().length(); i++ )
            {
                key.append( (char) ch );

                if ( ch != getEscapeString().charAt( i ) || ch == -1 || ( ch == '\n' && !supportMultiLineFiltering ) )
                {
                    // mismatch, EOF or EOL, no escape string here
                    in.reset();
                    inEscape = false;
                    key.setLength( 0 );
                    break;
                }

                ch = in.read();

            }

        }

        // have we found a delimiter?
        int max = 0;
        for ( DelimiterSpecification spec : delimiters )
        {
            String begin = spec.getBegin();

            // longest match wins
            if ( begin.length() < max )
            {
                continue;
            }

            for ( int i = 0; i < begin.length(); i++ )
            {
                if ( ch != begin.charAt( i ) || ch == -1 || ( ch == '\n' && !supportMultiLineFiltering ) )
                {
                    // mismatch, EOF or EOL, no match
                    break;
                }

                if ( i == begin.length() - 1 )
                {

                    beginToken = spec.getBegin();
                    endToken = spec.getEnd();

                }

                ch = in.read();

            }

            in.reset();
            in.skip( key.length() );
            ch = in.read();

        }

        // escape means no luck, prevent parsing of the escaped character, and return
        if ( inEscape )
        {

            if ( beginToken != null )
            {
                if ( !isPreserveEscapeString() )
                {
                    key.setLength( 0 );
                }
            }

            beginToken = null;
            endToken = null;

            key.append( (char) ch );

            replaceData = key.toString();
            replaceIndex = key.length();

            return read();

        }

        // no match means no luck, reset and return
        if ( beginToken == null || beginToken.length() == 0 || endToken == null || endToken.length() == 0 )
        {

            in.reset();
            return in.read();

        }

        // we're committed, find the end token, EOL or EOF

        key.append( beginToken );
        in.reset();
        in.skip( beginToken.length() );
        ch = in.read();

        int endTokenSize = endToken.length();
        int end = endTokenSize;
        do
        {
            if ( ch == -1 )
            {
                break;
            }
            else if ( ch == '\n' && !supportMultiLineFiltering )
            {
                // EOL
                key.append( (char) ch );
                break;
            }

            key.append( (char) ch );

            if ( ch == this.endToken.charAt( endTokenSize - end ) )
            {
                end--;
                if ( end == 0 )
                {
                    break;
                }
            }
            else
            {
                end = endTokenSize;
            }

            ch = in.read();
        }
        while ( true );

        // reset back to no tokens
        beginToken = null;
        endToken = null;

        // found endtoken? interpolate our key resolved above
        String value = null;
        if ( end == 0 )
        {
            try
            {
                if ( interpolateWithPrefixPattern )
                {
                    value = interpolator.interpolate( key.toString(), "", recursionInterceptor );
                }
                else
                {
                    value = interpolator.interpolate( key.toString(), recursionInterceptor );
                }
            }
            catch ( InterpolationException e )
            {
                IllegalArgumentException error = new IllegalArgumentException( e.getMessage() );
                error.initCause( e );

                throw error;
            }
        }
        else
        {
            // no endtoken? Write current char and continue in search for next expression
            in.reset();
            return in.read();
        }

        // write away the value if present, otherwise the key unmodified
        if ( value != null )
        {
            replaceData = value;
            replaceIndex = value.length();
        }
        else
        {
            replaceData = key.toString();
            replaceIndex = key.length();
        }

        if ( ch == -1 )
        {
            eof = true;
        }
        return read();

    }

    /**
     * @return interpolate with prefix pattern {@code true} (active) {@code false} otherwise.
     */
    public boolean isInterpolateWithPrefixPattern()
    {
        return interpolateWithPrefixPattern;
    }

    /**
     * @param interpolateWithPrefixPattern set the interpolate with prefix pattern.
     */
    public void setInterpolateWithPrefixPattern( boolean interpolateWithPrefixPattern )
    {
        this.interpolateWithPrefixPattern = interpolateWithPrefixPattern;
    }

    /**
     * @return {@link RecursionInterceptor}
     */
    public RecursionInterceptor getRecursionInterceptor()
    {
        return recursionInterceptor;
    }

    /**
     * @param givenRecursionInterceptor {@link RecursionInterceptor}
     * @return this
     */
    // CHECKSTYLE_OFF: LineLength
    public AbstractFilterReaderLineEnding setRecursionInterceptor( RecursionInterceptor givenRecursionInterceptor )
    // CHECKSTYLE_ON: LineLength
    {
        this.recursionInterceptor = givenRecursionInterceptor;
        return this;
    }

}
