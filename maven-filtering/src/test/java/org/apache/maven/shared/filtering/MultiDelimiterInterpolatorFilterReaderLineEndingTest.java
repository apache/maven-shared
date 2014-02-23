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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.codehaus.plexus.interpolation.Interpolator;
import org.codehaus.plexus.interpolation.RecursionInterceptor;
import org.codehaus.plexus.util.IOUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class MultiDelimiterInterpolatorFilterReaderLineEndingTest extends AbstractInterpolatorFilterReaderLineEndingTest
{

    @Mock 
    private Interpolator interpolator;

    @Before
    public void onSetup()
    {
        MockitoAnnotations.initMocks( this );
    }
    
    @Override
    protected Reader getAaa_AaaReader( Reader in, Interpolator interpolator )
    {
        MultiDelimiterInterpolatorFilterReaderLineEnding reader = new MultiDelimiterInterpolatorFilterReaderLineEnding( in, interpolator, true );
        reader.setDelimiterSpecs( Collections.singleton( "aaa*aaa" ) );
        return reader;
    }
    
    @Override
    protected Reader getAbc_AbcReader( Reader in, Interpolator interpolator )
    {
        MultiDelimiterInterpolatorFilterReaderLineEnding reader = new MultiDelimiterInterpolatorFilterReaderLineEnding( in, interpolator, true );
        reader.setDelimiterSpecs( Collections.singleton( "abc*abc" ) );
        return reader;
    }

    // MSHARED-199: Filtering doesn't work if 2 delimiters are used on the same line, the first one being left open
    @Test
    public void testLineWithSingleAtAndExpression()
        throws Exception
    {
        when( interpolator.interpolate( eq( "${foo}" ), eq( "" ), isA( RecursionInterceptor.class ) ) ).thenReturn( "bar" );

        Reader in = new StringReader( "toto@titi.com ${foo}" );
        MultiDelimiterInterpolatorFilterReaderLineEnding reader =
            new MultiDelimiterInterpolatorFilterReaderLineEnding( in, interpolator, true );
        reader.setDelimiterSpecs( new HashSet<String>( Arrays.asList( "${*}", "@" ) ) );
        
        assertEquals( "toto@titi.com bar", IOUtil.toString( reader ) );
    }
    
    // http://stackoverflow.com/questions/21786805/maven-war-plugin-customize-filter-delimitters-in-webresources/
    @Test
    public void testAtDollarExpression() throws Exception
    {
        when( interpolator.interpolate( eq( "${db.server}" ), eq( "" ), isA( RecursionInterceptor.class ) ) ).thenReturn( "DB_SERVER" );
        when( interpolator.interpolate( eq( "${db.port}" ), eq( "" ), isA( RecursionInterceptor.class ) ) ).thenReturn( "DB_PORT" );
        when( interpolator.interpolate( eq( "${db.name}" ), eq( "" ), isA( RecursionInterceptor.class ) ) ).thenReturn( "DB_NAME" );
        
        Reader in = new StringReader( "  url=\"jdbc:oracle:thin:\\@${db.server}:${db.port}:${db.name}\"" );
        MultiDelimiterInterpolatorFilterReaderLineEnding reader =
                        new MultiDelimiterInterpolatorFilterReaderLineEnding( in, interpolator, true );
        reader.setEscapeString( "\\" );
        reader.setDelimiterSpecs( new HashSet<String>( Arrays.asList( "${*}", "@" ) ) );
        
        assertEquals( "  url=\"jdbc:oracle:thin:@DB_SERVER:DB_PORT:DB_NAME\"", IOUtil.toString( reader ) );
    }

}
