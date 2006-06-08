package org.apache.maven.shared.io.download;

import junit.framework.TestCase;

public class DownloadFailedExceptionTest
    extends TestCase
{
    
    public void testShouldConstructWithUrlAndMessage()
    {
        new DownloadFailedException( "http://www.google.com", "can't find." );
    }

    public void testShouldConstructWithUrlMessageAndException()
    {
        new DownloadFailedException( "http://www.google.com", "can't find.", new NullPointerException() );
    }

    public void testShouldRetrieveUrlFromConstructor()
    {
        String url = "http://www.google.com";
        assertEquals( url, new DownloadFailedException( url, "can't find." ).getUrl() );
    }

}
