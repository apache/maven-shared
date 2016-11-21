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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

import java.io.Reader;
import java.io.StringReader;

import org.codehaus.plexus.interpolation.Interpolator;
import org.codehaus.plexus.interpolation.RecursionInterceptor;
import org.codehaus.plexus.util.IOUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public abstract class AbstractInterpolatorFilterReaderLineEndingTest
{

    @Mock
    private Interpolator interpolator;

    @Before
    public void onSetup()
    {
        MockitoAnnotations.initMocks( this );
    }

    @Test
    public void testDefaults()
        throws Exception
    {
        when( interpolator.interpolate( eq( "${a}" ), eq( "" ), isA( RecursionInterceptor.class ) ) ).thenReturn( "DONE_A" );

        Reader in = new StringReader( "text without expression" );
        Reader reader = getDollarBracesReader( in, interpolator, "\\" );
        assertEquals( "text without expression", IOUtil.toString( reader ) );

        in = new StringReader( "valid expression ${a}" );
        reader = getDollarBracesReader( in, interpolator, null );
        assertEquals( "valid expression DONE_A", IOUtil.toString( reader ) );

        in = new StringReader( "empty expression ${}" );
        reader = getDollarBracesReader( in, interpolator, null );
        assertEquals( "empty expression ${}", IOUtil.toString( reader ) );

        in = new StringReader( "dollar space expression $ {a}" );
        reader = getDollarBracesReader( in, interpolator, "\\" );
        assertEquals( "dollar space expression $ {a}", IOUtil.toString( reader ) );

        in = new StringReader( "space in expression ${ a}" );
        reader = getDollarBracesReader( in, interpolator, "\\" );
        assertEquals( "space in expression ${ a}", IOUtil.toString( reader ) );

        in = new StringReader( "escape dollar with expression \\${a}" );
        reader = getDollarBracesReader( in, interpolator, "\\" );
        assertEquals( "escape dollar with expression ${a}", IOUtil.toString( reader ) );

//        in = new StringReader( "escape escape string before expression \\\\${a}" );
//        reader = getDollarBracesReader( in, interpolator, "\\" );
//        assertEquals( "escape escape string before expression \\DONE_A", IOUtil.toString( reader ) );
//
//        in = new StringReader( "escape escape string and expression \\\\\\${a}" );
//        reader = getDollarBracesReader( in, interpolator, "\\" );
//        assertEquals( "escape escape string before expression \\${a}", IOUtil.toString( reader ) );

        in = new StringReader( "unknown expression ${unknown}" );
        reader = getDollarBracesReader( in, interpolator, "\\" );
        assertEquals( "unknown expression ${unknown}", IOUtil.toString( reader ) );
    }

    // MSHARED-198: custom delimiters doesn't work as expected
    @Test
    public void testCustomDelimiters()
        throws Exception
    {
        when( interpolator.interpolate( eq( "aaaFILTER.a.MEaaa" ), eq( "" ), isA( RecursionInterceptor.class ) ) ).thenReturn( "DONE" );
        when( interpolator.interpolate( eq( "abcFILTER.a.MEabc" ), eq( "" ), isA( RecursionInterceptor.class ) ) ).thenReturn( "DONE" );

        Reader in = new StringReader( "aaaFILTER.a.MEaaa" );
        Reader reader = getAaa_AaaReader( in, interpolator );

        assertEquals( "DONE", IOUtil.toString( reader ) );

        in = new StringReader( "abcFILTER.a.MEabc" );
        reader = getAbc_AbcReader( in, interpolator );
        assertEquals( "DONE", IOUtil.toString( reader ) );
    }

    // MSHARED-235: reader exceeds readAheadLimit
    @Test
    public void testMarkInvalid()
        throws Exception
    {
        Reader in = new StringReader( "@\").replace(p,\"]\").replace(q,\"" );
        Reader reader = getAtReader( in, interpolator, "\\" );

        assertEquals( "@\").replace(p,\"]\").replace(q,\"", IOUtil.toString( reader ) );
    }

    protected abstract Reader getAbc_AbcReader( Reader in, Interpolator interpolator );

    protected abstract Reader getAaa_AaaReader( Reader in, Interpolator interpolator );

    protected abstract Reader getDollarBracesReader( Reader in, Interpolator interpolator, String escapeString );

    protected abstract Reader getAtReader( Reader in, Interpolator interpolator, String escapeString );

}