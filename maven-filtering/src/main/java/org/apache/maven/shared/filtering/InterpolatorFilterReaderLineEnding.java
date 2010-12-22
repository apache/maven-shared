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

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.Interpolator;
import org.codehaus.plexus.interpolation.RecursionInterceptor;
import org.codehaus.plexus.interpolation.SimpleRecursionInterceptor;

/**
 * A FilterReader implementation, that works with Interpolator interface instead of it's own interpolation
 * implementation. This implementation is heavily based on org.codehaus.plexus.util.InterpolationFilterReader.
 *
 * @author cstamas
 * @author Olivier Lamy
 * @version $Id$
 * @since 1.0-beta-5
 */
public class InterpolatorFilterReaderLineEnding
    extends FilterReader
{

    /** Interpolator used to interpolate */
    private Interpolator interpolator;

    private RecursionInterceptor recursionInterceptor;

    /** replacement text from a token */
    private String replaceData = null;

    /** Index into replacement data */
    private int replaceIndex = -1;

    /** Index into previous data */
    private int previousIndex = -1;

    /** Default begin token. */
    public static final String DEFAULT_BEGIN_TOKEN = "${";

    /** Default end token. */
    public static final String DEFAULT_END_TOKEN = "}";
    
    private String beginToken;
    
    private String orginalBeginToken;
    
    private String endToken;
    
    /** true by default to preserve backward comp */
    private boolean interpolateWithPrefixPattern = true;

    private String escapeString;
    
    private boolean useEscape = false;
    
    /** if true escapeString will be preserved \{foo} -> \{foo} */
    private boolean preserveEscapeString = false;
    
    private boolean supportMultiLineFiltering;
        
    /**
     * @param in reader to use
     * @param interpolator interpolator instance to use
     * @param beginToken start token to use
     * @param endToken end token to use
     */
    public InterpolatorFilterReaderLineEnding( Reader in, Interpolator interpolator, String beginToken, String endToken, boolean supportMultiLineFiltering )
    {
        this( in, interpolator, beginToken, endToken, new SimpleRecursionInterceptor(), supportMultiLineFiltering );
    }    
    
    /**
     * @param in reader to use
     * @param interpolator interpolator instance to use
     * @param beginToken start token to use
     * @param endToken end token to use
     * @param ri The {@link RecursionInterceptor} to use to prevent recursive expressions.
     */
    private InterpolatorFilterReaderLineEnding( Reader in, Interpolator interpolator, String beginToken, String endToken, RecursionInterceptor ri, boolean supportMultiLineFiltering )
    {
        super( in );

        this.interpolator = interpolator;
        
        this.beginToken = beginToken;
        
        this.endToken = endToken;
        
        recursionInterceptor = ri;
        
        this.orginalBeginToken = this.beginToken;
        
        this.supportMultiLineFiltering = supportMultiLineFiltering;
    }    

    /**
     * Skips characters. This method will block until some characters are available, an I/O error occurs, or the end of
     * the stream is reached.
     *
     * @param n The number of characters to skip
     * @return the number of characters actually skipped
     * @exception IllegalArgumentException If <code>n</code> is negative.
     * @exception IOException If an I/O error occurs
     */
    public long skip( long n )
        throws IOException
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
     * @exception IOException If an I/O error occurs
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
     * @exception IOException if the underlying stream throws an IOException during reading
     */
    public int read()
        throws IOException
    {
        if ( replaceIndex != -1 && replaceIndex < replaceData.length() )
        {
            int ch = replaceData.charAt( replaceIndex++ );
            if ( replaceIndex >= replaceData.length() )
            {
                replaceIndex = -1;
            }
            return ch;
        }

        int ch = -1;
        if ( previousIndex != -1 && previousIndex < this.endToken.length() )
        {
            ch = this.endToken.charAt( previousIndex++ );
        }
        else
        {
            ch = in.read();
        }
        
        if (ch == '\n' && !supportMultiLineFiltering )
        {
            previousIndex = -1;
            return ch;
        }        
        
        if ( ch == this.beginToken.charAt( 0 ) || ( useEscape && ch == this.orginalBeginToken.charAt( 0 ) ) )
        {
            StringBuffer key = new StringBuffer( );

            key.append( (char) ch );

            int beginTokenMatchPos = 1;

            do
            {
                if ( previousIndex != -1 && previousIndex < this.endToken.length() )
                {
                    ch = this.endToken.charAt( previousIndex++ );
                }
                else
                {
                    ch = in.read();
                }
                if ( ch != -1 && (ch != '\n' && !supportMultiLineFiltering ) )
                {
                    key.append( (char) ch );
                    if ( ( beginTokenMatchPos < this.beginToken.length() )
                        && ( ch != this.beginToken.charAt( beginTokenMatchPos++ ) )
                        && ( useEscape && this.orginalBeginToken.length() > ( beginTokenMatchPos - 1 ) && ch != this.orginalBeginToken
                            .charAt( beginTokenMatchPos - 1 ) ) )
                    {
                        ch = -1; // not really EOF but to trigger code below
                        break;
                    }
                }
                else
                {
                    break;
                }
                // MSHARED-81 olamy : we must take care of token with length 1, escaping and same char : \@foo@
                // here ch == endToken == beginToken -> not going to next char : bad :-)
                if ( useEscape && this.orginalBeginToken == this.endToken && key.toString().startsWith( this.beginToken ) )
                {
                    ch = in.read();
                    key.append( (char) ch );
                }
            }
            while ( ch != this.endToken.charAt( 0 ) );

            // now test endToken
            if ( ch != -1 && this.endToken.length() > 1 )
            {
                int endTokenMatchPos = 1;

                do
                {
                    if ( previousIndex != -1 && previousIndex < this.endToken.length() )
                    {
                        ch = this.endToken.charAt( previousIndex++ );
                    }
                    else
                    {
                        ch = in.read();
                    }

                    if ( ch != -1 )
                    {
                        key.append( (char) ch );

                        if ( ch != this.endToken.charAt( endTokenMatchPos++ ) )
                        {
                            ch = -1; // not really EOF but to trigger code below
                            break;
                        }

                    }
                    else
                    {
                        break;
                    }
                }
                while ( endTokenMatchPos < this.endToken.length() );
            }

            // There is nothing left to read so we have the situation where the begin/end token
            // are in fact the same and as there is nothing left to read we have got ourselves
            // end of a token boundary so let it pass through.
            if ( ch == -1 || ( ch =='\n' && !supportMultiLineFiltering ) )
            {
                replaceData = key.toString();
                replaceIndex = 1;
                return replaceData.charAt( 0 );
            }

            String value = null;
            try
            {
                boolean escapeFound = false;
                if ( useEscape )
                {
                    if ( key.toString().startsWith( escapeString + orginalBeginToken ) )
                    {
                        String keyStr = key.toString();
                        if ( !preserveEscapeString )
                        {
                            value = keyStr.substring( escapeString.length(), keyStr.length() );
                        }
                        else
                        {
                            value = keyStr;
                        }
                        escapeFound = true;
                    }
                }
                if ( !escapeFound )
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
            }
            catch ( InterpolationException e )
            {
                IllegalArgumentException error = new IllegalArgumentException( e.getMessage() );
                error.initCause( e );

                throw error;
            }

            if ( value != null )
            {
                if ( value.length() != 0 )
                {
                    replaceData = value;
                    replaceIndex = 0;
                }
                return read();
            }
            else
            {
                previousIndex = 0;
                replaceData = key.substring( 0, key.length() - this.endToken.length() );
                replaceIndex = 0;
                return this.beginToken.charAt( 0 );
            }
        }

        return ch;
    }

    public boolean isInterpolateWithPrefixPattern()
    {
        return interpolateWithPrefixPattern;
    }

    public void setInterpolateWithPrefixPattern( boolean interpolateWithPrefixPattern )
    {
        this.interpolateWithPrefixPattern = interpolateWithPrefixPattern;
    }
    public String getEscapeString()
    {
        return escapeString;
    }

    public void setEscapeString( String escapeString )
    {
        // TODO NPE if escapeString is null ?
        if ( escapeString != null && escapeString.length() >= 1 )
        {
            this.escapeString = escapeString;
            this.orginalBeginToken = beginToken;
            this.beginToken = escapeString + beginToken;
            this.useEscape = escapeString != null && escapeString.length() >= 1;
        }
    }

    public boolean isPreserveEscapeString()
    {
        return preserveEscapeString;
    }

    public void setPreserveEscapeString( boolean preserveEscapeString )
    {
        this.preserveEscapeString = preserveEscapeString;
    }

    public RecursionInterceptor getRecursionInterceptor()
    {
        return recursionInterceptor;
    }

    public InterpolatorFilterReaderLineEnding setRecursionInterceptor( RecursionInterceptor recursionInterceptor )
    {
        this.recursionInterceptor = recursionInterceptor;
        return this;
    }
}
