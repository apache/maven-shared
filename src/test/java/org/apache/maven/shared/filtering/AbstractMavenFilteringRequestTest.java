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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashSet;

import org.junit.Test;

/**
 * @author Karl Heinz Marbaise <a href="mailto:khmarbaise@apache.org>khmarbaise@apache.org</a>.
 */
public class AbstractMavenFilteringRequestTest
{
    @Test
    public void setDelimitersShouldNotChangeAnythingIfUsingNull()
    {
        AbstractMavenFilteringRequest request = new AbstractMavenFilteringRequest();
        request.setDelimiters( null, false );
        assertThat( request.getDelimiters() ).containsExactly( "${*}", "@" );
    }

    @Test
    public void setDelimitersShouldNotChangeAnythingIfUsingEmpty()
    {
        AbstractMavenFilteringRequest request = new AbstractMavenFilteringRequest();
        LinkedHashSet<String> delimiters = new LinkedHashSet<String>();
        request.setDelimiters( delimiters, false );
        assertThat( request.getDelimiters() ).containsExactly( "${*}", "@" );
    }

    @Test
    public void setDelimitersShouldAddOnlyTheGivenDelimiter()
    {
        AbstractMavenFilteringRequest request = new AbstractMavenFilteringRequest();
        LinkedHashSet<String> delimiters = new LinkedHashSet<String>();
        delimiters.add( "test" );
        request.setDelimiters( delimiters, false );
        assertThat( request.getDelimiters() ).containsExactly( "test" );
    }

    @Test
    public void setDelimitersShouldAddDefaultDelimitersForNullElements()
    {
        AbstractMavenFilteringRequest request = new AbstractMavenFilteringRequest();
        LinkedHashSet<String> delimiters = new LinkedHashSet<String>();
        delimiters.add( "test" );
        delimiters.add( null );
        delimiters.add( "second" );
        request.setDelimiters( delimiters, false );
        assertThat( request.getDelimiters() ).containsExactly( "test", "${*}", "second" );
    }

    @Test
    public void setDelimitersShouldAddDefaultDelimitersIfUseDefaultDelimitersIfNullGiven()
    {
        AbstractMavenFilteringRequest request = new AbstractMavenFilteringRequest();
        request.setDelimiters( null, true );
        assertThat( request.getDelimiters() ).containsExactly( "${*}", "@" );
    }

    @Test
    public void setDelimitersShouldAddDefaultDelimitersIfUseDefaultDelimitersIfNotNullGiven()
    {
        AbstractMavenFilteringRequest request = new AbstractMavenFilteringRequest();
        LinkedHashSet<String> delimiters = new LinkedHashSet<String>();
        request.setDelimiters( delimiters, true );
        assertThat( request.getDelimiters() ).containsExactly( "${*}", "@" );
    }

    @Test
    public void setDelimitersShouldAddDefaultDelimitersIfUseDefaultDelimitersIfSingleElementIsGiven()
    {
        AbstractMavenFilteringRequest request = new AbstractMavenFilteringRequest();
        LinkedHashSet<String> delimiters = new LinkedHashSet<String>();
        delimiters.add( "test" );
        request.setDelimiters( delimiters, true );
        assertThat( request.getDelimiters() ).containsExactly( "${*}", "@", "test" );
    }

    @Test
    public void setDelimitersShouldAddDefaultDelimitersForNullElement()
    {
        AbstractMavenFilteringRequest request = new AbstractMavenFilteringRequest();
        LinkedHashSet<String> delimiters = new LinkedHashSet<String>();
        delimiters.add( "test" );
        delimiters.add( null );
        delimiters.add( "second" );
        request.setDelimiters( delimiters, true );
        assertThat( request.getDelimiters() ).containsExactly( "${*}", "@", "test", "second" );
    }

}