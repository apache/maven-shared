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

public class UrlValidationUtilTest
    extends TestCase
{

    public void testUrlWithPortIsAccepted()
    {
        testUrlIsAccepted( "http://host.organization.com:8080/something" );
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

    public void testAuthorityHostDotCompanyDotLocalIsRejected()
    {
        testAuthorityIsRejected( "host.organization.local" );
    }

    public void testAuthorityHostDotLocalIsRejected()
    {
        testAuthorityIsRejected( "host.local" );
    }

    public void testAuthorityWithStandardHttpPortIsAccepted()
    {
        testAuthorityIsAccepted( "host.organization.com:80" );
    }

    public void testAuthorityWithStandardHttpsPortIsAccepted()
    {
        testAuthorityIsAccepted( "host.organization.com:443" );
    }

    public void testAuthorityWithPort8080IsAccepted()
    {
        testAuthorityIsAccepted( "host.organization.com:8080" );
    }

    public void testAuthorityWithPortHighPortIsAccepted()
    {
        testAuthorityIsAccepted( "host.organization.com:55555" );
    }

    public void testAuthorityWithPort59999IsAccepted()
    {
        testAuthorityIsAccepted( "host.organization.com:59999" );
    }

    public void testAuthorityWithPort60000IsAccepted()
    {
        testAuthorityIsAccepted( "host.organization.com:60000" );
    }

    public void testAuthorityWithPort6000IsAccepted()
    {
        testAuthorityIsAccepted( "host.organization.com:6000" );
    }

    // This is a bug in Commons Validator VALIDATOR-411
    public void testAuthorityWithPort100000IsRejected()
    {
        //testAuthorityIsRejected( "host.organization.com:100000" );
    }

    public void testAuthorityWithLeadingZeroInPortIsAccepted()
    {
        // Though this looks awkward, RFC 3986, Section 3.2.3 says
        // "port = *DIGIT" whereas digit is 0 to 9
        testAuthorityIsAccepted( "host.organization.com:080" );
    }

    public void testAuthorityWithTrainlingDotIsAccepted()
    {
        testAuthorityIsAccepted( "host.com." );
    }

    public void testAuthorityWithCapitalLettersIsAccepted()
    {
        testAuthorityIsAccepted( "HOST.oRGaNiZAtION.cOm" );
    }

    public void testAuthorityIPIsAccepted()
    {
        testAuthorityIsAccepted( "1.2.3.4" );
    }

    public void testAuthorityWithLeadingDotIsRejected()
    {
        testAuthorityIsRejected( ".host.organization.com" );
    }

    public void testAuthorityOnlyConsistingOfLocalIsAccepted()
    {
        testAuthorityIsAccepted( "local" );
    }

    public void testAuthorityWithEmptySubDomainIsRejected()
    {
        testAuthorityIsRejected( "host..com" );
    }

    public void testAuthorityWithNonLocalDomainIsAccepted()
    {
        testAuthorityIsAccepted( "www.example.org" );
    }

    public void testAuthorityWithLeadingHyphenIsRejected()
    {
        testAuthorityIsRejected( "host.-organization.com" );
    }

    public void testAuthorityWithTrailingHyphenIsRejected()
    {
        testAuthorityIsRejected( "host.organization-.com" );
    }

    public void testAuthorityWithTooLongSubDomainIsRejected()
    {
        String tooLongDomainName = "aaaaaaaaaabbbbbbbbbbccccccccccddddddddddeeeeeeeeeeffffffffffabcd";
        assertTrue( tooLongDomainName.length() == 64 );
        testAuthorityIsRejected( "host." + tooLongDomainName + ".com" );
    }

    private void testAuthorityIsAccepted( final String input )
    {
        assertTrue( isValidAuthority( input ) );
    }

    private boolean isValidAuthority( final String input )
    {
        return UrlValidationUtil.isValidUrl( "http://" + input );
    }

    private void testAuthorityIsRejected( final String input )
    {
        assertFalse( isValidAuthority( input ) );
    }

}
