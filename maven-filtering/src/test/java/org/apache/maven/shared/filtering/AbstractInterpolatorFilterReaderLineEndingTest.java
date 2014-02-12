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
    
    protected abstract Reader getAbc_AbcReader( Reader in, Interpolator interpolator );
    
    protected abstract Reader getAaa_AaaReader( Reader in, Interpolator interpolator );

}