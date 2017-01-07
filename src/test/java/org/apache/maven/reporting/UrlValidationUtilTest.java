package org.apache.maven.reporting;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import junit.framework.TestCase;

import org.apache.commons.validator.routines.RegexValidator;

public class UrlValidationUtilTest
    extends TestCase
{

    public void testUrlWithPortIsAccepted()
    {
        testUrlIsAccepted( "http://host.organization.local:8080/something" );
    }

    public void testUrlWithLocalhostIsAccepted()
    {
        testUrlIsAccepted( "http://localhost/something" );
    }

    public void testUrlWithLocalhostAndPortIsAccepted()
    {
        testUrlIsAccepted( "http://localhost:8080/something" );
    }

    public void testUrlWithIpIsAccepted()
    {
        testUrlIsAccepted( "http://1.2.3.4/something" );
    }

    public void testUrlWithIpAndPortIsAccepted()
    {
        testUrlIsAccepted( "http://1.2.3.4:8080/something" );
    }

    public void testUrlWithHostNameLocalIsAccepted()
    {
        testUrlIsAccepted( "http://local/something" );
    }

    public void testUrlWithDotParisTopLevelDomainIsAccepted()
    {
        testUrlIsAccepted( "http://cool.project.paris/" );
    }

    public void testUrlWithDotAcademyTopLevelDomainIsAccepted()
    {
        testUrlIsAccepted( "http://cool.project.academy/" );
    }

    public void testUrlWithUnknownTopLevelDomainIsAccepted()
    {
        testUrlIsRejected( "http://cool.project.foobarisnotarealtld/" );
    }

    private void testUrlIsAccepted( final String string )
    {
        assertTrue( UrlValidationUtil.isValidUrl( string ) );
    }

    private void testUrlIsRejected( final String string )
    {
        assertFalse( UrlValidationUtil.isValidUrl( string ) );
    }

    public void testAuthorityHostDotCompanyDotLocalIsAccepted()
    {
        testAuthorityIsAccepted( "host.organization.local" );
    }

    public void testAuthorityHostDotLocalIsAccepted()
    {
        testAuthorityIsAccepted( "host.local" );
    }

    public void testAuthorityWithStandardHttpPortIsAccepted()
    {
        testAuthorityIsAccepted( "host.organization.local:80" );
    }

    public void testAuthorityWithStandardHttpsPortIsAccepted()
    {
        testAuthorityIsAccepted( "host.organization.local:443" );
    }

    public void testAuthorityWithPort8080IsAccepted()
    {
        testAuthorityIsAccepted( "host.organization.local:8080" );
    }

    public void testAuthorityWithPortHighPortIsAccepted()
    {
        testAuthorityIsAccepted( "host.organization.local:55555" );
    }

    public void testAuthorityWithPort59999IsAccepted()
    {
        testAuthorityIsAccepted( "host.organization.local:59999" );
    }

    public void testAuthorityWithPort60000IsRejected()
    {
        testAuthorityRejects( "host.organization.local:60000" );
    }

    public void testAuthorityWithPort6000IsAccepted()
    {
        testAuthorityIsAccepted( "host.organization.local:6000" );
    }

    public void testAuthorityWithPort100000IsRejected()
    {
        testAuthorityRejects( "host.organization.local:100000" );
    }

    public void testAuthorityWithLeadingZeroInPortIsRejected()
    {
        testAuthorityRejects( "host.organization.local:080" );
    }

    public void testAuthorityWithTrainlingDotIsAccepted()
    {
        testAuthorityIsAccepted( "host.local." );
    }

    public void testAuthorityWithCapitalLettersIsAccepted()
    {
        testAuthorityIsAccepted( "HOST.oRGaNiZAtION.LOcaL" );
    }

    public void testAuthorityIPIsRejected()
    {
        testAuthorityRejects( "1.2.3.4" );
    }

    public void testAuthorityWithLeadingDotIsRejected()
    {
        testAuthorityRejects( ".host.organization.local" );
    }

    public void testAuthorityOnlyConsistingOfLocalIsRejected()
    {
        testAuthorityRejects( "local" );
    }

    public void testAuthorityWithEmptySubDomainIsRejected()
    {
        testAuthorityRejects( "host..local" );
    }

    public void testAuthorityWithNonLocalDomainIsRejected()
    {
        testAuthorityRejects( "www.example.org" );
    }

    public void testAuthorityWithLeadingHyphenIsRejected()
    {
        testAuthorityRejects( "host.-organization.local" );
    }

    public void testAuthorityWithTrailingHyphenIsRejected()
    {
        testAuthorityRejects( "host.organization-.local" );
    }

    public void testAuthorityWithTooLongSubDomainIsRejected()
    {
        String tooLongDomainName = "aaaaaaaaaabbbbbbbbbbccccccccccddddddddddeeeeeeeeeeffffffffffabcd";
        assertTrue( tooLongDomainName.length() == 64 );
        testAuthorityRejects( "host." + tooLongDomainName + ".local" );
    }

    private void testAuthorityIsAccepted( final String input )
    {
        assertTrue( isValidAuthority( input ) );
    }

    private boolean isValidAuthority( final String input )
    {
        RegexValidator authority = UrlValidationUtil.configureLocalAuthorityValidator();
        return authority.isValid( input );
    }

    private void testAuthorityRejects( final String input )
    {
        assertFalse( isValidAuthority( input ) );
    }

}
